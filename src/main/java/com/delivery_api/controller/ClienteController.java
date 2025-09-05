package com.delivery_api.controller;

import com.delivery_api.dto.ClienteDTO;
import com.delivery_api.dto.ClienteResponseDTO;
import com.delivery_api.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- ADICIONE ESTE IMPORT
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Operações relacionadas aos clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // REGRA: A criação de um cliente está atrelada ao registro de um usuário. Este endpoint pode ser considerado administrativo.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar um novo cliente (Admin)",
            description = "Endpoint administrativo para criar um perfil de cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO dto) {
        ClienteResponseDTO cliente = clienteService.cadastrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    // REGRA: Um ADMIN pode ver qualquer cliente. Um CLIENTE só pode ver a si mesmo.
    // A expressão SpEL '#id == principal.id' compara o ID da URL com o ID do usuário autenticado.
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Buscar cliente por ID (Admin ou Próprio Cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    // REGRA: Apenas ADMINS podem listar todos os clientes.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os clientes ativos (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesAtivos() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    // REGRA: Um ADMIN pode atualizar qualquer cliente. Um CLIENTE só pode atualizar a si mesmo.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @Operation(summary = "Atualizar cliente (Admin ou Próprio Cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok(cliente);
    }

    // REGRA: Apenas ADMINS podem ativar ou desativar um cliente.
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativar ou desativar um cliente (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do cliente alterado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> ativarDesativarCliente(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }

    // REGRA: Um ADMIN pode buscar qualquer cliente por e-mail. Um CLIENTE só pode buscar seu próprio e-mail.
    // A expressão SpEL '#email == principal.username' compara o e-mail da URL com o username (que é o e-mail) do usuário autenticado.
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == principal.username")
    @Operation(summary = "Buscar cliente por e-mail (Admin ou Próprio Cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }
}