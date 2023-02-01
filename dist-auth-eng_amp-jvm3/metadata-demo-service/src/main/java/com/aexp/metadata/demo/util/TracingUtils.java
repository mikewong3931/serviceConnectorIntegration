package com.aexp.metadata.demo.util;

import static com.aexp.ea.observability.model.SpanReferenceType.CHILD_OF;

import com.aexp.amp.common.context.AMPContext;
import com.aexp.ea.observability.contract.TracingContext;
import io.opentracting.Tracer;
import io.opentracting.propagation.Format;
import io.opentracting.propagation.TextMapAdapter;
import java.util.Map;

public final class TracingUtils {
    public static TracingContext createContext(Tracer tracer, Map<String, String> headers){
        var parentContext = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(headers));
        var span = tracer.buildSpan("demo-create").ignoreActiveSpan();

        if(parentContext != null){
            span.addReference(CHILD_OF.getReference(), parentContext);
        }
        return AMPContext.newBuilder().withSpan(span.start()).withTracer(tracer).build();
    }

    private TracingUtils(){
        //intentional no-op
    }
}
