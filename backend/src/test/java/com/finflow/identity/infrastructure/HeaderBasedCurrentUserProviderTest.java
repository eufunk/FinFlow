package com.finflow.identity.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HeaderBasedCurrentUserProviderTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void readsUserIdFromHeader() {
        UUID userId = UUID.randomUUID();
        when(request.getHeader(HeaderBasedCurrentUserProvider.HEADER_NAME)).thenReturn(userId.toString());

        var provider = new HeaderBasedCurrentUserProvider(request);

        assertThat(provider.currentUserId()).isEqualTo(userId);
    }

    @Test
    void throwsWhenHeaderMissing() {
        when(request.getHeader(HeaderBasedCurrentUserProvider.HEADER_NAME)).thenReturn(null);

        var provider = new HeaderBasedCurrentUserProvider(request);

        assertThatThrownBy(provider::currentUserId).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void throwsWhenHeaderIsNotAValidUuid() {
        when(request.getHeader(HeaderBasedCurrentUserProvider.HEADER_NAME)).thenReturn("not-a-uuid");

        var provider = new HeaderBasedCurrentUserProvider(request);

        assertThatThrownBy(provider::currentUserId).isInstanceOf(IllegalStateException.class);
    }
}
