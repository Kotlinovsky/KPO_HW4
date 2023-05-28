package io.kotlinovsky.restaurant.gateway.filters

import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono

/**
 * Фильтр, отвечающий за переадресацию на Swagger-документацию микросервиса.
 * Был реализован отдельно, т.к. GatewayWebFilter не распространяется на контроллеры самого узла.
 */
@Order(-1)
@Component
class SwaggerFilter : WebFilter {

    private val pathPattern = PathPatternParser.defaultInstance.parse("/v3/api-docs/**")
    private val rewriteFilter = RewritePathGatewayFilterFactory().apply(RewritePathGatewayFilterFactory.Config().apply {
        regexp = "/v3/api-docs/service-(?<path>.*)"
        replacement = "/\$\\{path}/v3/api-docs"
    })

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (pathPattern.matches(exchange.request.path)) {
            return rewriteFilter.filter(exchange) { chain.filter(it) }
        }

        return chain.filter(exchange)
    }
}
