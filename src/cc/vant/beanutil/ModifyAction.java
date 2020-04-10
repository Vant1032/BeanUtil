package cc.vant.beanutil;

import cc.vant.beanutil.runnable.AddAnnotation;
import cc.vant.beanutil.utils.Constants;
import cc.vant.beanutil.utils.Utils;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;


public class ModifyAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = Utils.getPsiClassFromContext(e);
        if (psiClass == null) {
            return;
        }

        List<PsiField> nonStaticFiled = getNonStaticFiled(psiClass);

        Project thisProject = psiClass.getProject();
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(thisProject);
//        elementFactory.createa
        List<Runnable> toAdd = new ArrayList<>();
        for (PsiField psiField : nonStaticFiled) {
            PsiType type = psiField.getType();
            if (psiField.getAnnotation(Constants.JSON_FIELD_FULL_NAME) == null) {
                // 没有注解则添加
                String canonicalText = type.getCanonicalText();
                PsiAnnotation annotation = null;
                if ("java.lang.Integer".equals(canonicalText) || "java.lang.Short".equals(canonicalText) ||
                        "java.lang.Byte".equals(canonicalText) || "java.lang.Long".equals(canonicalText) ||
                        "java.math.BigDecimal".equals(canonicalText) || "java.math.BigInteger".equals(canonicalText)
                || "java.lang.Double".equals(canonicalText) || "java.lang.Float".equals(canonicalText)) {
                    annotation = elementFactory.createAnnotationFromText("@" + Constants.JSON_FIELD_FULL_NAME + "(" + Constants.SERIALIZE_NUM + ")", psiField);
                } else if ("java.lang.Boolean".equals(canonicalText)){
                    annotation = elementFactory.createAnnotationFromText("@" + Constants.JSON_FIELD_FULL_NAME + "(" + Constants.SERIALIZE_BOOL + ")", psiField);
                } else if ("java.util.Date".equals(canonicalText)) {
                    annotation = elementFactory.createAnnotationFromText("@" + Constants.JSON_FIELD_FULL_NAME + "(" + Constants.SERIALIZE_DATE + ")", psiField);
                } else {
                    continue;
                }

                toAdd.add(new AddAnnotation(annotation, psiField));
            } else {
                // 有注解则修改
                PsiAnnotation annotation = psiField.getAnnotation(Constants.JSON_FIELD_FULL_NAME);
                PsiAnnotationParameterList parameterList = annotation.getParameterList();
                PsiNameValuePair[] attributes = parameterList.getAttributes();
                for (PsiNameValuePair attribute : attributes) {
                    String attributeName = attribute.getAttributeName();
                    JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
                    System.out.println("attributeName = " + attributeName);
                    System.out.println("attributeValue = " + attributeValue);
                    ;
                }

            }
        }
        WriteCommandAction.runWriteCommandAction(thisProject, new FinalRunnable(toAdd));
    }


    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = Utils.getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    /**
     * 寻找要加上注解的filed
     */
    private List<PsiField> getNonStaticFiled(PsiClass psiClass) {
        List<PsiField> validField = new ArrayList<>();
        PsiField[] allFields = psiClass.getFields();
        for (PsiField field : allFields) {
            if (field.hasModifier(JvmModifier.STATIC)) {
                continue;
            }
            validField.add(field);
        }
        return validField;
    }
}
