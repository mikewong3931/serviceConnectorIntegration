package com.aexp.metadata.demo.util;
import com.aexp.amp.metadata.models.PaymentEventPayload;
import com.aexp.amp.security.model.ProtectedData;
import com.aexp.amp.security.model.UnprotectedData;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public final class Constants {
    public static final TypeReference<List<ProtectedData>> PROTECTED_DATA_TYPE_REF =
            new TypeReference<>();
    public static final TypeReference<List<UnprotectedData>> UNPROTECTED_DATA_TYPE_REF =
            new TypeReference<>();
    public static final TypeReference<List<PaymentEventPayload>> PAYMENT_EVENT_PAYLOAD_TYPE_REF =
            new TypeReference<>();

    public static final String PAYMENT_EVENT_PAYLOAD_KEY = "paymentEventPayload";
    public static final String PROTECTED_DATA_KEY = "protectedData";
    public static final String UNPROTECTED_DATA_KEY = "unprotectedData";
}
