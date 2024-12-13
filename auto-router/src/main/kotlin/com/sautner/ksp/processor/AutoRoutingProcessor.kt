package com.sautner.ksp.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.sautner.autorouter.AutoGet
import com.sautner.autorouter.AutoPost
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import io.ktor.server.application.*
import io.ktor.server.routing.*
import jdk.internal.net.http.frame.Http2Frame.asString
import java.io.OutputStream
import java.util.*
import kotlin.reflect.KClass

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

lateinit var logger: KSPLogger

private val processed = mutableSetOf<String>()

class AutoRoutingProcessor(val env: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger = env.logger
        val symbols = resolver.getSymbolsWithAnnotation("com.sautner.ksp.annotations.AutoRouting")
        val sequence = symbols.filter { it is KSClassDeclaration && it.validate() }
        if (sequence.toList().isNotEmpty()) {
            createRouter(sequence)
        }



//        log("processing ${sequence.toLsi .size} classes......")
//        sequence.forEach {
//            log("Code Generator Receiving ${it.javaClass.simpleName} ${it.validate()}")
//        }
//        if (sequence.isNotEmpty()) {
//             createRouter(sequence)
//        }

//
//            .forEach { it.accept(BuilderVisitor(), Unit) }
        return sequence.toList()
    }

    private fun createRouter(sequence: Sequence<KSAnnotated>) {
        log("starting code generation ${env.options}")
        val className = env.options["RouterClassName"]
        val classPackage = env.options["RouterPackage"]
        if (className.isNullOrBlank() or classPackage.isNullOrBlank()) {
            logger.error("You need to set the class and package name for autorouting in your ktor server's build.gradle for example: \n\n" +
                    "ksp {\n" +
                    "    arg(\"RouterClassName\", \"MyAutoRouter\")\n" +
                    "    arg(\"RouterPackage\", \"com.example.ktor\")\n" +
                    "}")
        } else {
            if (!processed.contains("${classPackage}.${className}")) {
                val routerClass = ClassName(classPackage!!, className!!)
                val file = FileSpec.builder(classPackage, className)
                addImports(file, sequence)  
                    
                    file.addFunction(FunSpec
                        .builder("autoRoute")
                        .receiver(Application::class)
                        .addCode(buildCodeBlock(sequence))
                        .build()
                    )

                file.build().writeTo(env.codeGenerator, false)
                processed.add("${classPackage}.${className}")
            }
        }


    }

    private fun addImports(file: FileSpec.Builder, sequence: Sequence<KSAnnotated>) {
        file.addImport("io.ktor.server.routing", "routing")
            .addImport("io.ktor.server.resources", "get", "post", "delete", "put")
            .addImport("io.ktor.util.reflect", "TypeInfo")



        sequence.toList().forEach {

                val import = (it as KSClassDeclaration).getImport()
                file.addImport(import.first, import.second)

                val annotationClass = getAutoRoutingKClassName(it)
                annotationClass?.let {
                    file.addImport(annotationClass.first, annotationClass.second)
                }

            }
        }
    }

    private fun buildCodeBlock(sequence: Sequence<KSAnnotated>): CodeBlock {
        val builder = CodeBlock.builder()
        builder
            .beginControlFlow("routing")
            .add(buildRouteCodeBlock(sequence))
            .endControlFlow()

        return builder.build()
    }

/**
 *     get<Actor.Actor1> {
 *              call.respond(it.render.invoke() as Test)
 *         }
 */
    private fun buildRouteCodeBlock(sequence: Sequence<KSAnnotated>): CodeBlock {
        val builder = CodeBlock.builder()
        sequence.toList().forEach {
            (it as KSClassDeclaration).let { ksc ->

                val getBlock = CodeBlock.builder()
                log(ksc.qualifiedName?.asString() ?: ksc.packageName.asString())
                if (ksc.implementsInterface(AutoGet::class)) {
                    val target = getAutoRoutingKClassName(ksc)
                    log("target $target")
                    getBlock.beginControlFlow("get<${ksc.simpleName.asString()}>")
                    //getBlock.add("call.respond(it.render.invoke())")
                    val responseClass = getAutoRoutingKClassName(ksc)
                    responseClass?.let {
                        getBlock.addStatement("val r = it.render.invoke()")
                        getBlock.beginControlFlow("r?.let")
                        getBlock.addStatement(" call.respond(r as ${responseClass.second}, typeInfo = TypeInfo(${responseClass.second}::class))")
                        getBlock.endControlFlow()
                    }
                    getBlock.endControlFlow()
                }
                if (ksc.implementsInterface(AutoPost::class)) {
                    getBlock.beginControlFlow("post<${ksc.simpleName.asString()}>")
                    getBlock.endControlFlow()
                }
                builder.add(getBlock.build())
            }
        }

        return builder.build()

    }

    fun KSClassDeclaration.getImport() : Pair<String, String> {
        val qualifiedName = this.qualifiedName?.asString()
            ?: throw IllegalArgumentException("Class declaration must have a qualified name")

        val packageName = qualifiedName.substringBeforeLast(".")
        val className = qualifiedName.substringAfterLast(".")
       return Pair(packageName, className)

    }

fun getAutoRoutingKClassName(classDeclaration: KSClassDeclaration): Pair<String, String>? {
    // Find the AutoRouting annotation

    classDeclaration.annotations.forEach {
        log(it.shortName.asString())
    }

    val autoRoutingAnnotation = classDeclaration.annotations
        .firstOrNull { it.shortName.asString() == "AutoRouting" }


    // If the annotation is present, retrieve its argument
    autoRoutingAnnotation?.arguments?.forEach { argument: KSValueArgument ->

        if (argument.name?.getShortName() == "kClass") {
            val kClassReference = argument.value

            if (kClassReference is KSType) {
                val param = kClassReference.declaration
                val qualifiedName = param.qualifiedName?.asString()

                param.packageName.let { packageName ->
                    param.simpleName.let { simpleNameName ->
                        return Pair(packageName.asString(), simpleNameName.asString())
                    }
                }


            }

        }
    }

    return null
}
// Function to check if the class or any of its superclasses implement the given interface
fun KSClassDeclaration.implementsInterface(interfaceClass: KClass<*>): Boolean {
    val interfaceFqn = interfaceClass.qualifiedName ?: return false

    fun KSClassDeclaration.checkSuperTypes(): Boolean {
        // Check if any of the super types match the interface
        return this.superTypes.any { superTypeRef: KSTypeReference ->
            val resolvedType: KSType = superTypeRef.resolve()
            val declaration = resolvedType.declaration as? KSClassDeclaration

            // Check if the resolved type's qualified name matches the interface
            if (resolvedType.declaration.qualifiedName?.asString() == interfaceFqn) {
                return true
            }

            // Recursively check the super types of the current super class
            declaration?.checkSuperTypes() ?: false
        }
    }

    return checkSuperTypes()
}

    private fun log(text: Any) {
        logger.info("CG: ${Date()} $text")
    }





