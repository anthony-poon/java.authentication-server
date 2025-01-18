package com.anthonypoon.authenicationserver.service.token;

import org.junit.jupiter.api.Disabled;

@Disabled("Refactoring")
public class AuthTokenServiceTest {
//    @Test
//    public void testCanDehydrateInput() throws AuthTokenException, TenantNotFoundException {
//        var encoder = getEncoder("some-encoded-token");
//        var service = new AuthTokenService(encoder, getRealmMock());
//        var token = AccessToken.builder()
//                .identifier("some-identifier")
//                .realm("some-realm-id")
//                .build();
//
//        var actual = service.dehydrate(token, 100);
//        verify(encoder).encode(
//                eq(Map.of("identifier", "some-identifier")),
//                eq("some-realm-secret"),
//                eq(100)
//        );
//        assertEquals("some-encoded-token", actual);
//    }
//
//    @Test
//    public void testCenRehydrateInput() throws AuthTokenException, TenantNotFoundException {
//        var decoder = getDecoder("some-encoded-token", Map.of(
//                "_type", "ACCESS",
//                "exp", ZonedDateTime.now().plusDays(1).toInstant().getEpochSecond(),
//                "identifier", "some-identifier"
//        ));
//        var service = new AuthTokenService(decoder, getRealmMock());
//
//        var actual = service.rehydrate("some-encoded-token", "some-realm-id", AccessToken.class);
//        assertInstanceOf(AccessToken.class, actual);
//        assertEquals(actual.getIdentifier(), "some-identifier");
//    }
//
//    private static Encoder getEncoder(String rtn) {
//        var encoder = Mockito.mock(Encoder.class);
//        when(encoder.encode(anyMap(), eq("some-realm-secret"), anyInt())).thenReturn(rtn);
//        return encoder;
//    }
//
//    private static Encoder getDecoder(String expectation, Map<String, Object> rtn) {
//        var decoder = Mockito.mock(Encoder.class);
//        when(decoder.decode(eq(expectation), eq("some-realm-secret"))).thenReturn(rtn);
//        return decoder;
//    }
//
//    private TenantService getRealmMock() throws TenantNotFoundException {
//        var realm = TenantData.builder()
//                .clientId("some-realm-id")
//                .tokenSecret("some-realm-secret")
//                .build();
//        var service = mock(TenantService.class);
//        when(service.getById(eq("some-realm-id"))).thenReturn(realm);
//        return service;
//    }
}
