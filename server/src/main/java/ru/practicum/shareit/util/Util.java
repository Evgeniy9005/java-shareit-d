package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class Util {

    public static PageRequest createPageParam(int from, int size) {
       // validFromSize(from,size);
        PageRequest pageRequest;
        if (from > 0) {
            pageRequest = PageRequest.of(from / size,size);
            log.info("{}",pageRequest);
            return pageRequest;
        }

        pageRequest = PageRequest.of(from,size);//по умолчанию
        log.info("{}",pageRequest);
        return PageRequest.of(from,size);
    }


    public static PageRequest createPageParam(int from, int size, Sort sort) {

       // validFromSize(from,size);
        PageRequest pageRequest;
        if (from > 0) {
            pageRequest = PageRequest.of(from / size,size,sort);
            log.info("{}",pageRequest);
            return pageRequest;
        }
        pageRequest = PageRequest.of(from,size,sort); //по умолчанию
        log.info("{}",pageRequest);
        return pageRequest;
    }

    public static int start(int from, int size) {
      //  validFromSize(from,size);

        if (from > 0) {
            log.info("start {}",from  % size);
            return from  % size;
        }

        return 0;
    }

    /*private static void validFromSize(int from, int size) {
        if (from < 0 || size < 1) {
            throw new BadRequestException(
                    "Не верно заданы входные параметры для отображения данных в диапазоне от # до #  ", from, size);
        }
    }*/

    public static <T> List<T> getElementsFrom(List<T> list,int start) {

        return list.stream().skip(start).collect(Collectors.toList());
    }
}
