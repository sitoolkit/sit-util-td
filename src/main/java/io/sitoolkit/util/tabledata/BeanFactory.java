package io.sitoolkit.util.tabledata;

public interface BeanFactory {

    <T> T getBean(String beanId, Class<T> type);
}
