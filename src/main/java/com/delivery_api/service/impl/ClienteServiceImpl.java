package com.delivery_api.service.impl;

import com.delivery_api.dto.ClienteDTO;
import com.delivery_api.dto.ClienteResponseDTO;
import com.delivery_api.model.Cliente;
import com.delivery_api.exception.BusinessException;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.repository.ClienteRepository;
import com.delivery_api.service.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

   @Override
    @Transactional
    public ClienteResponseDTO cadastrarCliente(ClienteDTO dto) {
        // 1. VERIFICA PRIMEIRO
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Já existe um cliente cadastrado com o e-mail: " + dto.getEmail());
        }

        // 2. SÓ CONTINUA SE A VERIFICAÇÃO PASSAR
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setAtivo(true);
        Cliente novoCliente = clienteRepository.save(cliente);

        return modelMapper.map(novoCliente, ClienteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com email: " + email));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        // Validar email único (se mudou)
        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        // Atualizar dados
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        cliente.setAtivo(!cliente.isAtivo());
        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientesAtivos() {
        List<Cliente> clientesAtivos = clienteRepository.findByAtivoTrue();
        return clientesAtivos.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }
}