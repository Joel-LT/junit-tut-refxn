package tastemprunner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class testCaseInfo{
    private String id,label,file,testSuite;
    public testCaseInfo(String id, String label, String file, String testSuite) {
        this.id = id;
        this.label = label;
        this.file = file;
        this.testSuite = testSuite;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getFile() {
        return file;
    }

    public String getTestSuite() {
        return testSuite;
    }
}
class testSuiteInfo{
    private String id,label,file;

    public testSuiteInfo(String id, String label, String file) {
        this.id = id;
        this.label = label;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getFile() {
        return file;
    }
}

public class Main {

    public static String getMd5(String input) {
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

    public static JSONObject getTestCaseJSON(testCaseInfo testcaseinfo){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", testcaseinfo.getId());
        jsonObject.put("label",testcaseinfo.getLabel());
        jsonObject.put("file", testcaseinfo.getFile());
        jsonObject.put("test-suite", testcaseinfo.getTestSuite());
        return jsonObject;
    }

    public static JSONObject getTestSuiteJSON(testSuiteInfo testsuiteinfo){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", testsuiteinfo.getId());
        jsonObject.put("label",testsuiteinfo.getLabel());
        jsonObject.put("file", testsuiteinfo.getFile());
        return jsonObject;
    }

    public static void writeInJSONFile(JSONObject discoveryjson) throws IOException {
        try(FileWriter file = new FileWriter("discovery.json")){
            file.write(discoveryjson.toString(2));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Class[] classArray = getClasses("calculator");
        JSONObject discoveryjson = new JSONObject();
        JSONArray testCasesArray = new JSONArray();
        JSONArray testSuitesArray = new JSONArray();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Class currentClass : classArray) {
            String classID="", classLabel="", filename="", testSuite="";
            filename = currentClass.getName();
            classID = getMd5(filename);

            for (Method method: currentClass.getDeclaredMethods()) {
                boolean isTestMethod = false;
                String methodID="", methodLabel="";
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Test) {
                        methodID = getMd5(method.getName());
                        isTestMethod = true;
                    }
                    if (annotation instanceof DisplayName) {
                        DisplayName myAnnotation = (DisplayName) annotation;
                        methodLabel = ((DisplayName) annotation).value();
                    }
                    else
                    {
                        methodLabel = method.getName();
                    }
                }
                if (isTestMethod){
                    testCaseInfo testcaseinfo = new testCaseInfo(methodID,methodLabel,filename,filename); // get the json of individual test case
                    JSONObject testcasejson = getTestCaseJSON(testcaseinfo);
//                    System.out.println("NEW TEST CASE:");
//                    System.out.println(testcasejson);
                    testCasesArray.put(testcasejson);
                    if (!map.containsKey(filename)){
                        map.put(filename, 0);
                    }
                    map.put(filename, map.get(filename) + 1);
                }

            }

            if (map.containsKey(filename)){
                classLabel = filename;
                Annotation[] annotations = currentClass.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof DisplayName) {
//                        DisplayName myAnnotation = (DisplayName) annotation;
                        classLabel = ((DisplayName) annotation).value();
                    }
                }
                testSuiteInfo testsuiteinfo = new testSuiteInfo(classID, classLabel, filename);
                JSONObject testsuitejson = getTestSuiteJSON(testsuiteinfo);
//                System.out.println("NEW TEST SUITE");
//                System.out.println(testsuitejson);
                testSuitesArray.put(testsuitejson);
            }

        }
        discoveryjson.put("testCases", testCasesArray);
        discoveryjson.put("testSuites", testSuitesArray);
        System.out.println(discoveryjson.toString(2));
        writeInJSONFile(discoveryjson);


//        JSONObject obj = new JSONObject();
//        obj.put("name","Joel");
//        obj.put("rno","123");
//        JSONArray list = new JSONArray();
//        list.put("Java");
//        list.put("C++");
//        list.put("Python");
//        obj.put("courses",list);
//        System.out.println(obj);

        }


    }
