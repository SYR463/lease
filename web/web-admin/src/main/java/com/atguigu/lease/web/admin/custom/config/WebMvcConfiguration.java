package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.web.admin.custom.converter.String2ItemTypeConverter;
import com.atguigu.lease.web.admin.custom.converter.StringToBaseEnumConverterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

//    @Autowired
//    private String2ItemTypeConverter string2ItemTypeConverter;

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {

//        registry.addConverter(this.string2ItemTypeConverter);
        registry.addConverterFactory(this.stringToBaseEnumConverterFactory);
    }
}
