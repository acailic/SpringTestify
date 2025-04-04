package io.github.springtestify.processor;

import com.google.auto.service.AutoService;
import io.github.springtestify.annotation.ScenarioTest;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("io.github.springtestify.annotation.ScenarioTest")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ScenarioTestProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ScenarioTest.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@ScenarioTest can only be applied to classes",
                    element
                );
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            ScenarioTest annotation = element.getAnnotation(ScenarioTest.class);

            try {
                generateConstructor(typeElement, annotation);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to generate constructor: " + e.getMessage(),
                    element
                );
            }
        }
        return true;
    }

    private void generateConstructor(TypeElement typeElement, ScenarioTest annotation) throws IOException {
        String packageName = processingEnv.getElementUtils()
            .getPackageOf(typeElement)
            .getQualifiedName()
            .toString();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "Generated")
            .addModifiers(Modifier.PUBLIC)
            .superclass(TypeName.get(typeElement.asType()));

        // Get the entity class type mirror safely
        TypeMirror entityType = null;
        try {
            annotation.value(); // This will throw MirroredTypeException
        } catch (javax.lang.model.type.MirroredTypeException mte) {
            entityType = mte.getTypeMirror();
        }

        if (entityType == null) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Could not determine entity type from @ScenarioTest annotation",
                typeElement
            );
            return;
        }

        // Add constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addStatement("super($T.class)", TypeName.get(entityType))
            .build();

        classBuilder.addMethod(constructor);

        // Generate the class
        JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build())
            .build();

        // Write the file
        JavaFileObject sourceFile = processingEnv.getFiler()
            .createSourceFile(packageName + "." + typeElement.getSimpleName() + "Generated");

        try (Writer writer = sourceFile.openWriter()) {
            javaFile.writeTo(writer);
        }
    }
}
