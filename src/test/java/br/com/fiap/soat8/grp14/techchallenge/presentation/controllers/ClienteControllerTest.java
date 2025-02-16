package br.com.fiap.soat8.grp14.techchallenge.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.soat8.grp14.techchallenge.app.dto.cliente.ClienteDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.dto.cliente.ClienteInsertDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.services.ClienteService;

public class ClienteControllerTest {

    private MockMvc mockMvc;
    private ClienteService clienteService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        clienteService = Mockito.mock(ClienteService.class);
        ClienteController clienteController = new ClienteController(clienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSalvarCliente() throws Exception {
        ClienteInsertDTO clienteInsertDTO = new ClienteInsertDTO();
        clienteInsertDTO.setNome("Cliente Teste");
        clienteInsertDTO.setCpf("123.456.789-01");
        clienteInsertDTO.setEmail("cliente@teste.com");

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Teste");
        clienteDTO.setCpf("123.456.789-01");
        clienteDTO.setEmail("cliente@teste.com");

        when(clienteService.salvarCliente(any(ClienteInsertDTO.class))).thenReturn(clienteDTO);

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteInsertDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetClientes() throws Exception {
        when(clienteService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetClientePorCpf() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();

        when(clienteService.buscarCliente(anyString())).thenReturn(clienteDTO);

        mockMvc.perform(get("/api/clientes/cpf/{cpf}", "123.456.789-01"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetClientePorId() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();

        when(clienteService.buscarClienteId(anyLong())).thenReturn(clienteDTO);

        mockMvc.perform(get("/api/clientes/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void testAtualizarCliente() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Atualizado");
        clienteDTO.setCpf("123.456.789-01");
        clienteDTO.setEmail("cliente@atualizado.com");

        when(clienteService.atualizarCliente(anyLong(), any(ClienteDTO.class))).thenReturn(clienteDTO);

        mockMvc.perform(put("/api/clientes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeletarCliente() throws Exception {
        mockMvc.perform(delete("/api/clientes/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}