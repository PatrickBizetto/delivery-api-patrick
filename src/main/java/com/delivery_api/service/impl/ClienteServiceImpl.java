package com.delivery_api.service.impl;

import com.delivery_api.dto.ClienteDTO;
import com.delivery_api.dto.ClienteResponseDTO;
import com.delivery_api.exception.BusinessException;
import com.delivery_api.exception.ConflictException;
import com.delivery_api.exception.EntityNotFoundException;
import com.delivery_api.model.Cliente;
import com.delivery_api.repository.ClienteRepository;
import com.delivery_api.service.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
// 🔹 Imports necessários para o cache
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    /**
     * Cadastra um novo cliente.
     * Após o sucesso, invalida todo o cache "clientes" para garantir que
     * a lista de clientes e outras buscas reflitam o novo cadastro.
     */
    @Override
    @Transactional
    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteResponseDTO cadastrarCliente(ClienteDTO dto) {
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Já existe um cliente cadastrado com o e-mail: " + dto.getEmail());
        }

        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setAtivo(true);
        Cliente novoCliente = clienteRepository.save(cliente);

        return modelMapper.map(novoCliente, ClienteResponseDTO.class);
    }

    /**
     * Busca um cliente pelo ID.
     * O resultado será armazenado no cache "clientes" usando o ID como chave.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "clientes", key = "#id")
    public ClienteResponseDTO buscarClientePorId(Long id) {
        System.out.println("### BUSCANDO CLIENTE DO BANCO DE DADOS (ID: " + id + ") ###");
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    /**
     * Busca um cliente pelo email.
     * O resultado será armazenado no cache "clientes" usando o email como chave.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "clientes", key = "#email")
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        System.out.println("### BUSCANDO CLIENTE DO BANCO DE DADOS (EMAIL: " + email + ") ###");
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com email: " + email));
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    /**
     * Lista todos os clientes ativos.
     * O resultado (a lista) será armazenado no cache "clientes" com uma chave estática.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "clientes", key = "'listaClientesAtivos'")
    public List<ClienteResponseDTO> listarClientesAtivos() {
        System.out.println("### BUSCANDO LISTA DE CLIENTES ATIVOS DO BANCO DE DADOS ###");
        List<Cliente> clientesAtivos = clienteRepository.findByAtivoTrue();
        return clientesAtivos.stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Atualiza um cliente existente.
     * Invalida todo o cache "clientes" para garantir que dados desatualizados sejam removidos.
     */
    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        if (!cliente.getEmail().equals(dto.getEmail()) && clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        Cliente clienteAtualizado = clienteRepository.save(cliente);
        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    /**
     * Altera o status (ativo/inativo) de um cliente.
     * Invalida todo o cache "clientes", pois o status do cliente afeta a lista de ativos.
     */
    @Override
    @CacheEvict(value = "clientes", allEntries = true)
    public ClienteResponseDTO ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));

        cliente.setAtivo(!cliente.isAtivo());
        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }
}