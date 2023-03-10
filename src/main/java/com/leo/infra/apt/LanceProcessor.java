package com.leo.infra.apt;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

//指定处理的注解
@SupportedAnnotationTypes({"com.leo.infra.apt.annotation.TestAnnotation"})
//指定处理的版本
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LanceProcessor extends AbstractProcessor {
    
    
    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        return false;
    }
    
  
}