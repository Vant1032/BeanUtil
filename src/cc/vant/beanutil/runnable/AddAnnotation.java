package cc.vant.beanutil.runnable;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * 直接在相应字段上添加相应注解的动作
 */
public class AddAnnotation implements Runnable {
    private PsiAnnotation annotation;
    private PsiField psiField;

    public AddAnnotation(PsiAnnotation annotation, PsiField psiField) {
        this.annotation = annotation;
        this.psiField = psiField;
    }

    @Override
    public void run() {
        PsiElement psiElement = psiField.addBefore(annotation, psiField);
        JavaCodeStyleManager.getInstance(psiField.getProject()).shortenClassReferences(psiElement);
    }
}
