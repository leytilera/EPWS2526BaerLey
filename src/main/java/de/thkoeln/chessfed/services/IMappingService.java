package de.thkoeln.chessfed.services;

import java.util.Map;

public interface IMappingService {
    
    <T> T parse(Map<String, Object> json, Class<T> clazz);

    <T> T map(Object obj, Class<T> clazz);

}
