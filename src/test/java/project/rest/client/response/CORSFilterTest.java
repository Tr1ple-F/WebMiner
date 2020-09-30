package project.rest.client.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CORSFilterTest {

    private ContainerResponseFilter instance;
    private ContainerRequestContext requestContext;
    private ContainerResponseContext responseContext;
    private MultivaluedMap<String, String> requestHeaders;
    private MultivaluedMap<String, Object> responseHeaders;
    private ArgumentCaptor<String> headCaptor;
    private ArgumentCaptor<Object> objectCaptor;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void init() {
        instance = new CORSFilter();
        requestContext = mock(ContainerRequestContext.class);
        responseContext = mock(ContainerResponseContext.class);
        requestHeaders = new MultivaluedHashMap<>();
        responseHeaders = (MultivaluedMap<String, Object>) mock(MultivaluedMap.class);
        headCaptor = ArgumentCaptor.forClass(String.class);
        objectCaptor = ArgumentCaptor.forClass(Object.class);
    }

    @Test
    void testPredefinedOrigin() throws IOException {
        requestHeaders.add("origin", "test");
        performMock();
        assertEquals(headCaptor.getAllValues().get(0), "Access-Control-Allow-Origin");
        assertEquals(objectCaptor.getAllValues().get(0), "test");
        checkCORS(headCaptor.getAllValues(), objectCaptor.getAllValues());
    }

    @Test
    void testWithoutOrigin() throws IOException {
        requestHeaders.add("origin", null);
        performMock();
        assertEquals(headCaptor.getAllValues().get(0), "Access-Control-Allow-Origin");
        assertEquals(objectCaptor.getAllValues().get(0), "*");
        checkCORS(headCaptor.getAllValues(), objectCaptor.getAllValues());
    }

    private void performMock() throws IOException{
        when(requestContext.getHeaders()).thenReturn(requestHeaders);
        when(responseContext.getHeaders()).thenReturn(responseHeaders);
        instance.filter(requestContext, responseContext);
        verify(responseContext, times(4)).getHeaders();
        verify(responseHeaders, times(4)).add(headCaptor.capture(), objectCaptor.capture());
    }

    private void checkCORS(List<String> headers, List<Object> values) {
        assertEquals(headers.get(1), "Access-Control-Allow-Headers");
        assertEquals(headers.get(2), "Access-Control-Allow-Credentials");
        assertEquals(headers.get(3), "Access-Control-Allow-Methods");
        assertEquals(values.get(1), "*");
        assertEquals(values.get(2), true);
        assertEquals(values.get(3), "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }

}
