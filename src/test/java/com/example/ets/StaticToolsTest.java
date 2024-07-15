//package com.example.ets;
//
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import static com.example.etc.StaticTools.dropSmallDecimalPart;
//
//
//public class StaticToolsTest {
//    @Test
//    public void dropSmallDecimalPartTest() {
//        Assertions.assertEquals(dropSmallDecimalPart(12.12345, 1), 12.1, 0.0);
//        Assertions.assertEquals(dropSmallDecimalPart(-45.461, 1), -45.5, 0.0);
//        Assertions.assertEquals(dropSmallDecimalPart(0.0001, 4), 0.0001, 0.0);
//        Assertions.assertEquals(dropSmallDecimalPart(1.8999, 1), 1.9, 0.0);
//        Assertions.assertEquals(dropSmallDecimalPart(1.9999, 1), 2.0, 0.0);
//
//
//    }
//}