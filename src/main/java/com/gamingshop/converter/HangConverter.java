package com.gamingshop.converter;

import com.gamingshop.entity.Hang;
import com.gamingshop.repository.HangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class HangConverter implements Converter<String, Hang> {

    @Autowired
    private HangRepository hangRepository;

    @Override
    public Hang convert(String id) {
        if (id == null || id.isEmpty()) return null;
        return hangRepository.findById(Long.parseLong(id)).orElse(null);
    }
}