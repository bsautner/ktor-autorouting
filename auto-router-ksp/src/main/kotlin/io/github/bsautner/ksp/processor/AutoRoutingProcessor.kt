package io.github.bsautner.ksp.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.bsautner.autorouter.AutoGet
import io.github.bsautner.autorouter.AutoMultipartPost
import io.github.bsautner.autorouter.AutoPost
import io.github.bsautner.autorouter.AutoWeb
import io.github.bsautner.autorouter.annotations.AutoRouting
import io.ktor.resources.*
import io.ktor.server.application.*
import java.util.*
import kotlin.reflect.KClass

/**
 * TODO - add sorting and organize the generated routes.
 * add kdocs
 *
 *
 *
 */

lateinit var logger: KSPLogger

private val processed = mutableSetOf<String>()

class AutoRoutingProcessor(val env: SymbolProcessorEnvironment) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger = env.logger
        val annotationFqName = Resource::class.qualifiedName!!
        val symbols = resolver.getSymbolsWithAnnotation(annotationFqName)
        val sequence = symbols.filter { it is KSClassDeclaration && it.validate() }
        if (sequence.toList().isNotEmpty()) {
            createRouter(sequence)
        }

        return sequence.toList()
    }

    private fun createRouter(sequence: Sequence<KSAnnotated>) {
        log("starting code generation ${env.options}")
        val className = AutoRouting::class.simpleName
        val classPackage = AutoRouting::class.qualifiedName?.substringBeforeLast(".")
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
            .addImport("io.ktor.server.html", "respondHtml")
            .addImport("kotlinx.html", "html", "body", "div")
            .addImport("io.github.bsautner.autorouter","getPostBodyClass", "getPostResponseBodyClass")
            .addImport("io.ktor.server.request", "receive")
            .addImport("io.ktor.server.request","receiveMultipart", "receiveParameters")




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
                        getBlock.addStatement(" call.respond(it.render.invoke() as ${responseClass.second}, typeInfo = TypeInfo(${responseClass.second}::class))")
                    }
                    getBlock.endControlFlow()
                }
                if (ksc.implementsInterface(AutoWeb::class)) {

                    /**
                     *   routing {
                     *         get<Website.HomePage> {
                     *             call.respondHtml {
                     *
                     *                 body {
                     *                     it.render.invoke(this)
                     *
                     *                 }
                     *             }
                     *
                     *         }
                     *     }
                     */

                    getBlock.beginControlFlow("get<${ksc.simpleName.asString()}>")

                    getBlock.beginControlFlow("call.respondHtml")
                    getBlock.beginControlFlow("body")
                    getBlock.addStatement("it.render.invoke(this)")
                    getBlock.endControlFlow().endControlFlow()

                  // getBlock.addStatement(" call.respond(it.render.invoke() as ${responseClass.second}, typeInfo = TypeInfo(${responseClass.second}::class))")

                    getBlock.endControlFlow()
                }

                if (ksc.implementsInterface(AutoMultipartPost::class)) {
                    /**
                     *       val form = call.receiveParameters()
                     *
                     *       call.respond( it.process(form))
                     */
                    val responseClass = getAutoRoutingKClassName(ksc)?.second
                    getBlock.beginControlFlow("post<${ksc.simpleName.asString()}>")
                    getBlock.addStatement("val form = call.receiveParameters()")
                        .addStatement(" call.respond(it.process(form) as Any, typeInfo = TypeInfo(Any::class))")

                    getBlock.endControlFlow()
                }

                if (ksc.implementsInterface(AutoPost::class)) {
                    /**
                     *   routing {
                     *         post<Sensor> {
                     *             val body = call.receive(it.getPostBodyClass())
                     *             val response = it.process(body as TestPostBody)
                     *             call.respond(response, TypeInfo(it.getPostResponseBodyClass()))
                     *         }
                     *     }
                     */
                    val responseClass = getAutoRoutingKClassName(ksc)?.second
                    if (responseClass?.isEmpty() == true) {
                       logger.error("AutoRouter processed a POST but the post body KClass is missing from the Annotation.")
                    }
                    getBlock.beginControlFlow("post<${ksc.simpleName.asString()}>")
                    getBlock.addStatement("val body = call.receive(it.getPostBodyClass())")
                        .addStatement("val response = it.process(body as ${responseClass})")
                        .addStatement("call.respond(response, TypeInfo(it.getPostResponseBodyClass()))")
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

        if (argument.name?.getShortName() == "serializableResponse") {
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





