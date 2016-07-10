/**
*
* @author Ashish Banerjee , 8-jan-2001
* (c) Osprey Software Technology P. Ltd., 2001, All rights reserved.
*  tested with jdk 1.3, will it work with jdk 1.2 ?
*/
/*
* 
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both

* (a) the Software, and

* (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a “Larger Work” to which the Software is contributed by such licensors),
* 
* without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms.
* 
* This license is subject to the following condition:
* 
* The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* 
*/

package twister.system;

import java.lang.reflect.*;
import java.util.Properties;
import java.util.Enumeration;

public class BeanSetter {
   public BeanSetter() {}

/**
    Method takes input as property file and a target object.
    It iterates the property file and checks for bean design pattern
    for setting property in the following order
    String, boolean, int , double
*/

   public int assignValues(Properties prop, Object target) throws Exception{
      int ret = 0;
      Enumeration xenum = prop.propertyNames();
      while(xenum.hasMoreElements()) {
         String propName = (String) xenum.nextElement();
         String propVal = prop.getProperty(propName);
         propName = propName.trim();
         if(propName.length() < 2 )
            continue;
         if(propVal != null)
            propVal = propVal.trim();
         String methodName = changeToSetPattern(propName);
         if(debug)
            System.out.println("assigning -->"+methodName);
         if(assignString(target,methodName, propVal))
             ret++;
		 else if (assignProperty(target,methodName, propVal))
             ret ++;
      }

      return ret;
   }
   public boolean assignObject(Object target,String methodName,Object objVal) throws Exception{
        boolean ret = false;
        Method method = getAssignableMethod(target,methodName,objVal.getClass());
        if(method != null) {
           method.invoke(target, new Object[] {objVal});
           ret = true;
        }
        return ret;

   }

   public boolean assignProperty(Object target,String name,String value) throws Exception {
        boolean ret = false;
        Class strtypz[] = new Class []  { String.class, String.class};
        Method method = getPropertySetterMethod(target,"setProperty",strtypz);
        if(method != null) {
           method.invoke(target, new Object[] {name, value});
           ret = true;
        }
        return ret;
   }
   public boolean assignString(Object target,String methodName,String  propVal) throws Exception{
        boolean ret = false;
        Method method = getAssignableMethod(target, methodName);
        if(method != null) {
            Object arg = toArg(propVal);
            method.invoke(target,new Object[] {arg});
            ret = true;
        }

        return ret;
   }
   public String changeToSetPattern(String propName) {
      return "set"+propName.substring(0,1).toUpperCase()+propName.substring(1);
   }
   private  java.lang.reflect.Method  getAssignableMethod(Object target, String cmd) throws Exception {
       return  getAssignableMethod(target,cmd,typz);
   }
   private  java.lang.reflect.Method  getAssignableMethod(Object target, String cmd, Class typ) throws Exception {
       return getAssignableMethod(target,cmd,new Class[] {typ});
   }
   private  java.lang.reflect.Method  getAssignableMethod(Object target, String cmd, Class[] typz) throws Exception {
         Method ret = null;
         Method[] mes = target.getClass().getMethods() ;
         Class [] argsClz = new Class[1];
         int i,j,ii;
         for(ii=0; (ii < typz.length) && ( ret == null); ii++) {
             typeIndex = ii;
             argsClz[0] = typz[ii];

             for(i=0;( i < mes.length) && ( ret == null); i++) {
                if(mes[i].getName().equals(cmd)) {
                    Class [] clzz = mes[i].getParameterTypes();
                    if(argsClz.length == clzz.length) {
                        boolean mismatch = false;
                        if(debug) {
                           System.out.println(" clzz["+clzz[0]+"] argsClz["+argsClz[0]+"]");
                           System.out.println("equals-->"+(argsClz[0].equals(clzz[0])));
                           System.out.println("clazz isAssignableFrom-->"+(clzz[0].isAssignableFrom(argsClz[0])));
                           System.out.println("argsClz isAssignableFrom-->"+(argsClz[0].isAssignableFrom(clzz[0])));
                        }
                        // 1st try exact match
                        if(!argsClz[0].equals(clzz[0]))
                           mismatch = true;

                        if(!mismatch) {
                           ret = mes[i];
                        }
                        else {
                           // next try partial match
                           mismatch = false;
                          if(!clzz[0].isAssignableFrom(argsClz[0]))
                             mismatch = true;

                           if(!mismatch) {
                             ret = mes[i];
                           }
                        }
                    } // fi arg lengths match
                } // fi name equals
             }// for i
     } // for ii

         return ret;

   }

   private  java.lang.reflect.Method  getPropertySetterMethod(Object target, String cmd, Class[] typz) throws Exception {
         Method ret = null;
         Method[] mes = target.getClass().getMethods() ;
         Class [] argsClz = new Class[2];
         int i,j,ii;
         for(ii=0; (ii < typz.length) && ( ret == null); ii++) {
             argsClz[ii] = typz[ii];
             argsClz[ii] = typz[ii];
		 }

		 for(i=0;( i < mes.length) && ( ret == null); i++) {
			if(mes[i].getName().equals(cmd)) {
				Class [] clzz = mes[i].getParameterTypes();
				if(argsClz.length == clzz.length) {
					boolean mismatch = false;

					// 1st try exact match
					if(!(argsClz[0].equals(clzz[0]) && argsClz[1].equals(clzz[1])))
					   mismatch = true;

					if(!mismatch) {
					   ret = mes[i];
					}
					else {
					   // next try partial match
					  mismatch = false;
					  if(!(clzz[0].isAssignableFrom(argsClz[0]) && clzz[0].isAssignableFrom(argsClz[0])))
						 mismatch = true;

					   if(!mismatch) {
						 ret = mes[i];
					   }
					}
				} // fi arg lengths match
			} // fi name equals
		 }// for i
      return ret;
   }


   private Object toArg(String propVal) throws Exception {
       Object ret = null;
       if(propVal != null) {
            Constructor ctor = typx[typeIndex].getConstructor(new Class [] { String.class});
            if(ctor != null)
               ret = ctor.newInstance(new Object[] {propVal});
       }
       return ret;
   }

   private int typeIndex = 0;
   private static Class [] typz = new Class [] {String.class, Integer.class, Double.class, Float.class,int.class,double.class, float.class,boolean.class,long.class,Long.class};
   private static Class [] typx = new Class [] {String.class, Integer.class, Double.class, Float.class,Integer.class,Double.class, Float.class, Boolean.class,Long.class,Long.class};

   public static boolean debug = false;
}
