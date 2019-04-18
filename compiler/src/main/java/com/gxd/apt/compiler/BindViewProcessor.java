package com.gxd.apt.compiler;

import com.google.auto.service.AutoService;
import com.gxd.apt.annotation.BindView;
import com.gxd.apt.annotation.DIActivity;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by guoxiaodong on 2019/4/16 13:33
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {// 规定需要处理的注解
        return Collections.singleton(DIActivity.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(DIActivity.class);
        for (Element element : elementSet) {
            if (element instanceof TypeElement) {// 判断是否Class
                List<? extends Element> memberList = elementUtils.getAllMembers(((TypeElement) element));
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("bind")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addParameter(ClassName.get(element.asType()), "activity");

                for (Element memberElement : memberList) {
                    BindView bindView = memberElement.getAnnotation(BindView.class);
                    if (bindView != null) {
                        methodSpecBuilder.addStatement(
                                "activity.$L = ($L)activity.findViewById($L)",
                                memberElement.getSimpleName(),
                                ClassName.get(memberElement.asType()).toString(),
                                bindView.value()
                        );
                    }
                }

                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(element.getSimpleName() + "_ViewBinding")
                        .superclass(TypeName.get(element.asType()))
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(methodSpecBuilder.build());

                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();

                JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build()).build();

                try {
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
