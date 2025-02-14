package br.com.fiap.soat8.grp14.techchallenge.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import br.com.fiap.soat8.grp14.techchallenge.app.dto.cliente.ClienteDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.dto.cliente.ClienteInsertDTO;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.Cliente;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.AtualizarClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.BuscarClienteCpfUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.BuscarClienteIdUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.CriarClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.ExcluirClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.ListarClienteUseCase;

public class ClienteServiceTest {

    private ClienteService clienteService;
    private CriarClienteUseCase criarClienteUseCase;
    private BuscarClienteCpfUseCase buscarClienteCpfUseCase;
    private ListarClienteUseCase listarClienteUseCase;
    private BuscarClienteIdUseCase buscarClienteIdUseCase;
    private ExcluirClienteUseCase excluirClienteUseCase;
    private AtualizarClienteUseCase atualizarClienteUseCase;
    private ModelMapper mapper;

    @BeforeEach
    public void setUp() {
        criarClienteUseCase = Mockito.mock(CriarClienteUseCase.class);
        buscarClienteCpfUseCase = Mockito.mock(BuscarClienteCpfUseCase.class);
        listarClienteUseCase = Mockito.mock(ListarClienteUseCase.class);
        buscarClienteIdUseCase = Mockito.mock(BuscarClienteIdUseCase.class);
        excluirClienteUseCase = Mockito.mock(ExcluirClienteUseCase.class);
        atualizarClienteUseCase = Mockito.mock(AtualizarClienteUseCase.class);
        mapper = new ModelMapper();
        clienteService = new ClienteService(criarClienteUseCase, buscarClienteCpfUseCase, listarClienteUseCase, buscarClienteIdUseCase, excluirClienteUseCase, atualizarClienteUseCase, mapper);
    }

    @Test
    public void testSalvarCliente() {
        ClienteInsertDTO clienteInsertDTO = new ClienteInsertDTO();
        clienteInsertDTO.setNome("Cliente Teste");
        clienteInsertDTO.setCpf("12345678901");
        clienteInsertDTO.setEmail("cliente@teste.com");

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Teste");
        clienteDTO.setCpf("12345678901");
        clienteDTO.setEmail("cliente@teste.com");

        when(criarClienteUseCase.execute(any())).thenReturn(mapper.map(clienteInsertDTO, Cliente.class));

        ClienteDTO result = clienteService.salvarCliente(clienteInsertDTO);
        assertEquals("Cliente Teste", result.getNome());
    }

    @Test
    public void testListarTodos() {
        when(listarClienteUseCase.execute(true)).thenReturn(Collections.emptyList());

        List<ClienteDTO> result = clienteService.listarTodos();
        assertEquals(0, result.size());
    }

    @Test
    public void testBuscarCliente() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Teste");

        when(buscarClienteCpfUseCase.execute(anyString())).thenReturn(mapper.map(clienteDTO, Cliente.class));

        ClienteDTO result = clienteService.buscarCliente("12345678901");
        assertEquals("Cliente Teste", result.getNome());
    }

    @Test
    public void testBuscarClienteId() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Teste");

        when(buscarClienteIdUseCase.execute(anyLong())).thenReturn(mapper.map(clienteDTO, Cliente.class));

        ClienteDTO result = clienteService.buscarClienteId(1L);
        assertEquals("Cliente Teste", result.getNome());
    }

    @Test
    public void testAtualizarCliente() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Atualizado");
        clienteDTO.setCpf("12345678901");
        clienteDTO.setEmail("cliente@atualizado.com");

        when(atualizarClienteUseCase.execute(any())).thenReturn(mapper.map(clienteDTO, Cliente.class));

        ClienteDTO result = clienteService.atualizarCliente(1L, clienteDTO);
        assertEquals("Cliente Atualizado", result.getNome());
    }

    @Test
    public void testExcluirCliente() {
        clienteService.excluirCliente(1L);
        Mockito.verify(excluirClienteUseCase).execute(1L);
    }
}