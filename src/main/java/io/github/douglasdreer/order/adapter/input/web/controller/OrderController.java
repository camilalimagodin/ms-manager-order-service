package io.github.douglasdreer.order.adapter.input.web.controller;

import io.github.douglasdreer.order.application.dto.CreateOrderCommand;
import io.github.douglasdreer.order.application.dto.OrderResponse;
import io.github.douglasdreer.order.application.port.input.CreateOrderUseCase;
import io.github.douglasdreer.order.application.port.input.GetOrderUseCase;
import io.github.douglasdreer.order.application.port.input.ProcessOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para operações de pedidos.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Gerenciamento de pedidos")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ProcessOrderUseCase processOrderUseCase;

    @Operation(
            summary = "Criar novo pedido",
            description = "Cria um novo pedido com os itens informados. O pedido é criado com status RECEIVED e totais calculados automaticamente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido criado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Pedido duplicado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderCommand command) {
        
        log.info("Requisição para criar pedido: externalOrderId={}", command.getExternalOrderId());
        
        OrderResponse response = createOrderUseCase.execute(command);
        
        log.info("Pedido criado: id={}", response.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Buscar pedido por ID",
            description = "Retorna os detalhes de um pedido específico incluindo todos os itens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID único do pedido", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        
        log.debug("Requisição para buscar pedido: id={}", id);
        
        return getOrderUseCase.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Buscar pedido por ID externo",
            description = "Retorna os detalhes de um pedido usando o identificador externo fornecido pelo sistema de origem."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @GetMapping(value = "/external/{externalOrderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> getOrderByExternalId(
            @Parameter(description = "ID externo do pedido", example = "EXT-ORDER-12345")
            @PathVariable String externalOrderId) {
        
        log.debug("Requisição para buscar pedido por ID externo: {}", externalOrderId);
        
        return getOrderUseCase.findByExternalOrderId(externalOrderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Listar pedidos por status",
            description = "Retorna uma lista de pedidos filtrados por status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pedidos retornada com sucesso"
            )
    })
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Status do pedido", example = "CALCULATED")
            @PathVariable String status) {
        
        log.debug("Requisição para listar pedidos por status: {}", status);
        
        List<OrderResponse> orders = getOrderUseCase.findByStatus(status);
        
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Listar todos os pedidos",
            description = "Retorna uma lista com todos os pedidos do sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pedidos retornada com sucesso"
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.debug("Requisição para listar todos os pedidos");
        
        List<OrderResponse> orders = getOrderUseCase.findAll();
        
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Processar pedido",
            description = "Inicia o processamento de um pedido. Transiciona o status para PROCESSING e calcula os totais."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido processado com sucesso",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Status inválido para processamento",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @PostMapping(value = "/{id}/process", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> processOrder(
            @Parameter(description = "ID único do pedido")
            @PathVariable UUID id) {
        
        log.info("Requisição para processar pedido: id={}", id);
        
        OrderResponse response = processOrderUseCase.process(id);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Marcar pedido como disponível",
            description = "Marca o pedido como disponível para consulta externa. Requer que o pedido esteja no status CALCULATED."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido marcado como disponível",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Status inválido para marcação como disponível",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @PatchMapping(value = "/{id}/available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> markAsAvailable(
            @Parameter(description = "ID único do pedido")
            @PathVariable UUID id) {
        
        log.info("Requisição para marcar pedido como disponível: id={}", id);
        
        OrderResponse response = processOrderUseCase.markAsAvailable(id);
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Marcar pedido como falha",
            description = "Marca o pedido como falha no processamento. Pode ser aplicado a qualquer pedido não finalizado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido marcado como falha",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    @PatchMapping(value = "/{id}/failed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> markAsFailed(
            @Parameter(description = "ID único do pedido")
            @PathVariable UUID id,
            @Parameter(description = "Motivo da falha", example = "Erro ao calcular total")
            @RequestParam(required = false, defaultValue = "Erro não especificado") String reason) {
        
        log.warn("Requisição para marcar pedido como falha: id={}, motivo={}", id, reason);
        
        OrderResponse response = processOrderUseCase.markAsFailed(id, reason);
        
        return ResponseEntity.ok(response);
    }
}
