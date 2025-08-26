package com.delivery_api.service;

import com.delivery_api.dto.ClienteDTO;
import com.delivery_api.dto.ClienteResponseDTO;
import java.util.List;

public interface ClienteService {

    ClienteResponseDTO cadastrarCliente(ClienteDTO dto);

    ClienteResponseDTO buscarClientePorId(Long id);

    ClienteResponseDTO buscarClientePorEmail(String email);

    ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto);

    ClienteResponseDTO ativarDesativarCliente(Long id);

    List<ClienteResponseDTO> listarClientesAtivos();
}