package org.y4sec.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Controller
@SuppressWarnings("all")
public class DemoController {
    private static final List<Object> BLACKLIST = Arrays.asList(
            "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet",
            "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",
            "com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter");

    @PostMapping("/demo")
    @ResponseBody
    public String demo(@RequestBody String data) {
        try {
            byte[] serData = Base64.getDecoder().decode(data);
            ByteArrayInputStream bin = new ByteArrayInputStream(serData);
            ObjectInputStream ois = new ObjectInputStream(bin) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc)
                        throws IOException, ClassNotFoundException {
                    if (BLACKLIST.contains(desc.getName())) {
                        throw new InvalidClassException(
                                "the class " + desc.getName() + " is on the blacklist");
                    } else {
                        return super.resolveClass(desc);
                    }
                }
            };
            Object obj = ois.readObject();
            System.out.println("object: " + obj.toString());
            ois.close();
            return "ok";
        } catch (Exception ex) {
            return ex.toString();
        }
    }
}
