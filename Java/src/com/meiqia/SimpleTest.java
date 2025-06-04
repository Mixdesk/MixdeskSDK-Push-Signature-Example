package com.meiqia;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SignatureException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SimpleTest {
    public static void main(String[] args) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("../sample.txt")));
            
            // 提取key
            Pattern keyPattern = Pattern.compile("\"key\":\\s*\"([^\"]+)\"");
            Matcher keyMatcher = keyPattern.matcher(json);
            if (!keyMatcher.find()) {
                System.out.println("无法找到key");
                return;
            }
            String key = keyMatcher.group(1);
            
            MixdeskSigner signer = new MixdeskSigner(key);
            
            // 提取所有的text和sign对
            Pattern testPattern = Pattern.compile("\"text\":\\s*\"([^\"]+)\",\\s*\"sign\":\\s*\"([^\"]+)\"");
            Matcher testMatcher = testPattern.matcher(json);
            
            int testCount = 0;
            int passCount = 0;
            
            while (testMatcher.find()) {
                testCount++;
                String text = testMatcher.group(1);
                String expectedSign = testMatcher.group(2);
                
                try {
                    String actualSign = signer.sign(text);
                    
                    if (expectedSign.equals(actualSign)) {
                        System.out.println("✅ Test " + testCount + " PASSED");
                        System.out.println("  Text: " + text);
                        passCount++;
                    } else {
                        System.out.println("❌ Test " + testCount + " FAILED");
                        System.out.println("  Text: " + text);
                        System.out.println("  Expected: " + expectedSign);
                        System.out.println("  Actual:   " + actualSign);
                    }
                } catch (SignatureException e) {
                    System.out.println("❌ Test " + testCount + " ERROR: " + e.getMessage());
                }
                System.out.println();
            }
            
            System.out.println("=== 测试结果 ===");
            System.out.println("总测试数: " + testCount);
            System.out.println("通过数: " + passCount);
            System.out.println("失败数: " + (testCount - passCount));
            if (testCount > 0) {
                System.out.println("通过率: " + (passCount * 100 / testCount) + "%");
            }
            
        } catch (Exception e) {
            System.out.println("运行测试时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 