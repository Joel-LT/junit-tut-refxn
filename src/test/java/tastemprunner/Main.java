package tastemprunner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Main {

    public static String getMd5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        List dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList classes = new ArrayList();
        for (Object directory : dirs) {
            classes.addAll(findClasses((File) directory, packageName));
        }
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }

    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Class[] c_= getClasses("calculator");
        for (Class x : c_){
            System.out.println(getMd5(x.getName()));
            System.out.println(x.getPackageName());
            for (Method m: x.getDeclaredMethods()){
                System.out.println(m.getName());
                Annotation[] annotations = m.getDeclaredAnnotations();
                for(Annotation annotation : annotations){
//                    System.out.println(annotation instanceof DisplayName);
                    if(annotation instanceof Test){
//                        Test myAnnotation = (Test) annotation;
                        System.out.println("IS TEST");
                    }
                    if(annotation instanceof DisplayName){
//                        DisplayName myAnnotation = (DisplayName) annotation;
                        System.out.println("Has a display name");
                    }
                }
            }
        }
    }


}
