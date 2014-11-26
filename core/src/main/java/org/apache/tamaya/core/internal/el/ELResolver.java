/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//package org.apache.tamaya.core.internal.el;
//
//import org.apache.tamaya.Configuration;
//import org.apache.tamaya.Environment;
//import org.apache.tamaya.core.spi.ExpressionResolver;
//
//import javax.el.*;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Properties;
//
///**
// * Created by Anatole on 28.09.2014.
// */
//public final class ELResolver implements ExpressionResolver{
//
//    private ExpressionFactory factory;
//
//
//    @Override
//    public String getResolverId() {
//        return "el";
//    }
//
//    @Override
//    public String resolve(String expression){
//        if(factory==null){
//            Properties props = new Properties();
//            props.setProperty("javax.el.ExpressionFactory", "org.apache.el.ExpressionFactoryImpl");
//            ExpressionFactory tmpFactory = null;
//            try {
//                tmpFactory = ExpressionFactory.newInstance(props);
//            } catch ( RuntimeException e ) {
//                System.err.println("Error creating EL expression factory.");
//            }
//            factory = tmpFactory;
//        }
//        ConfigurationContext context = new ConfigurationContext();
//        Configuration config = Configuration.of();
//        Objects.requireNonNull(config);
//        context.bind("config", config);
//        context.bind("env", Environment.of());
//        context.bind("system.env", System.getenv());
//        context.bind("system.prop", System.getProperties());
//        ValueExpression converted = factory.createValueExpression(context, expression, Object.class );
//        return String.valueOf(converted.getValue(context));
//
//    }
//
//    private class ConfigurationContext extends ELContext{
//
//        private final BeanELResolver resolver = new BeanELResolver();
//        private final FunctionMapper functionMapper = new NoopFunctionMapper();
//        private final VariableMapper variableMapper = new VariableMapperImpl();
//        private final Map<String,ValueExpression> variables = new HashMap<>();
//
//        @Override
//        public javax.el.ELResolver getELResolver(){
//            return resolver;
//        }
//
//        @Override
//        public FunctionMapper getFunctionMapper(){
//            return functionMapper;
//        }
//
//        @Override
//        public VariableMapper getVariableMapper(){
//            return variableMapper;
//        }
//
//        public void bind(final String variable, final Object obj){
//            variables.put(variable, new ValueExpression(){
//                @Override
//                public Object getValue(ELContext elContext){
//                    return obj;
//                }
//
//                @Override
//                public void setValue(ELContext elContext, Object o){
//                    // ignore
//                }
//
//                @Override
//                public boolean isReadOnly(ELContext elContext){
//                    return true;
//                }
//
//                @Override
//                public Class<?> getType(ELContext elContext){
//                    return variable.getClass();
//                }
//
//                @Override
//                public Class<?> getExpectedType(){
//                    return variable.getClass();
//                }
//
//                @Override
//                public String getExpressionString(){
//                    return null;
//                }
//
//                @Override
//                public boolean equals(Object o){
//                    return false;
//                }
//
//                @Override
//                public int hashCode(){
//                    return 0;
//                }
//
//                @Override
//                public boolean isLiteralText(){
//                    return false;
//                }
//            });
//        }
//
//        private class VariableMapperImpl extends VariableMapper{
//            public ValueExpression resolveVariable(String s){
//                return variables.get(s);
//            }
//
//            public ValueExpression setVariable(String s, ValueExpression valueExpression){
//                return (variables.put(s, valueExpression));
//            }
//        }
//
//        private class NoopFunctionMapper extends FunctionMapper{
//            public Method resolveFunction(String s, String s1){
//                return null;
//            }
//        }
//
//    }
//
//}
