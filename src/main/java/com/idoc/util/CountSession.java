package com.idoc.util;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.idoc.constant.LogConstant;
/*
 * Author @霍庆源
 * 这是一个简易的在线人数统计模块
 * onlinePeople是一个内存共享的原子类，用于记录session的个数
 * 实际上这种统计并不精确
 */
public class CountSession{
    public static AtomicInteger onlinePeople = new AtomicInteger(0);
//    public static AtomicInteger historyPeople = new AtomicInteger(0);
//    public static AtomicInteger todayPeople = new AtomicInteger(0);
}
