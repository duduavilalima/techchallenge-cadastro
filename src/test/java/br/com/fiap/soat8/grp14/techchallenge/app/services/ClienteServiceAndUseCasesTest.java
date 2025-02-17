package br.com.fiap.soat8.grp14.techchallenge.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.soat8.grp14.techchallenge.app.dto.cliente.ClienteDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.dto.cliente.ClienteInsertDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.exceptions.DataIntegrityException;
import br.com.fiap.soat8.grp14.techchallenge.app.exceptions.EntityNotFoundException;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.Cliente;
import br.com.fiap.soat8.grp14.techchallenge.core.interfaces.AbstractUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.AtualizarClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.BuscarClienteCpfUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.BuscarClienteIdUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.CriarClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.ExcluirClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.cliente.ListarClienteUseCase;
import br.com.fiap.soat8.grp14.techchallenge.data.models.ClienteEntity;
import br.com.fiap.soat8.grp14.techchallenge.data.repositories.ClienteRepository;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceAndUseCasesTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ModelMapper mapper = new ModelMapper();

    private CriarClienteUseCase criarClienteUseCase;
    private BuscarClienteCpfUseCase buscarClienteCpfUseCase;
    private BuscarClienteIdUseCase buscarClienteIdUseCase;
    private AtualizarClienteUseCase atualizarClienteUseCase;
    private ExcluirClienteUseCase excluirClienteUseCase;
    private ListarClienteUseCase listarClienteUseCase;

    private ClienteService clienteService;

    private void injectModelMapper(AbstractUseCase<?, ?> useCase, ModelMapper mapper) {
        try {
            Field field = AbstractUseCase.class.getDeclaredField("modelMapper");
            field.setAccessible(true);
            field.set(useCase, mapper);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao injetar ModelMapper no use case: " + useCase.getClass().getName(), e);
        }
    }

    @BeforeEach
    public void setUp() {
        criarClienteUseCase = new CriarClienteUseCase(clienteRepository);
        buscarClienteCpfUseCase = new BuscarClienteCpfUseCase(clienteRepository);
        buscarClienteIdUseCase = new BuscarClienteIdUseCase(clienteRepository);
        atualizarClienteUseCase = new AtualizarClienteUseCase(clienteRepository);
        listarClienteUseCase = new ListarClienteUseCase(clienteRepository);
        excluirClienteUseCase = new ExcluirClienteUseCase(clienteRepository, buscarClienteIdUseCase);

        injectModelMapper(criarClienteUseCase, mapper);
        injectModelMapper(buscarClienteCpfUseCase, mapper);
        injectModelMapper(buscarClienteIdUseCase, mapper);
        injectModelMapper(atualizarClienteUseCase, mapper);
        injectModelMapper(listarClienteUseCase, mapper);
        injectModelMapper(excluirClienteUseCase, mapper);

        clienteService = new ClienteService(
                criarClienteUseCase,
                buscarClienteCpfUseCase,
                listarClienteUseCase,
                buscarClienteIdUseCase,
                excluirClienteUseCase,
                atualizarClienteUseCase,
                mapper
        );
    }

    @Test
    public void testSalvarCliente_Success() {
        ClienteInsertDTO insertDTO = new ClienteInsertDTO();
        insertDTO.setNome("Teste");
        insertDTO.setCpf("11111111111");
        insertDTO.setEmail("teste@teste.com");

        ClienteEntity entity = mapper.map(insertDTO, ClienteEntity.class);
        when(clienteRepository.findByCpf("11111111111")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(entity);

        ClienteDTO result = clienteService.salvarCliente(insertDTO);

        assertEquals("Teste", result.getNome());
        assertEquals("11111111111", result.getCpf());
        assertEquals("teste@teste.com", result.getEmail());
    }

    @Test
    public void testSalvarCliente_DuplicateCpf() {
        ClienteInsertDTO insertDTO = new ClienteInsertDTO();
        insertDTO.setNome("Teste");
        insertDTO.setCpf("22222222222");
        insertDTO.setEmail("teste@teste.com");

        ClienteEntity existingEntity = new ClienteEntity();
        existingEntity.setCpf("22222222222");

        when(clienteRepository.findByCpf("22222222222")).thenReturn(Optional.of(existingEntity));

        assertThrows(DataIntegrityException.class, () -> {
            clienteService.salvarCliente(insertDTO);
        });
    }

    @Test
    public void testListarTodos_WithData() {
        ClienteEntity entity1 = new ClienteEntity();
        entity1.setId(1L);
        entity1.setNome("Cliente A");
        entity1.setCpf("123");
        entity1.setEmail("a@a.com");

        ClienteEntity entity2 = new ClienteEntity();
        entity2.setId(2L);
        entity2.setNome("Cliente B");
        entity2.setCpf("456");
        entity2.setEmail("b@b.com");

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<ClienteDTO> list = clienteService.listarTodos();

        assertEquals(2, list.size());
        assertEquals("Cliente A", list.get(0).getNome());
        assertEquals("Cliente B", list.get(1).getNome());
    }

    @Test
    public void testListarTodos_Empty() {
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        List<ClienteDTO> list = clienteService.listarTodos();

        assertTrue(list.isEmpty());
    }

    @Test
    public void testBuscarClienteCpf_Success() {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(1L);
        entity.setNome("Cliente Teste");
        entity.setCpf("33333333333");
        entity.setEmail("cliente@teste.com");

        when(clienteRepository.findByCpf("33333333333")).thenReturn(Optional.of(entity));

        ClienteDTO dto = clienteService.buscarCliente("33333333333");

        assertEquals("Cliente Teste", dto.getNome());
    }

    @Test
    public void testBuscarClienteCpf_NotFound() {
        when(clienteRepository.findByCpf("44444444444")).thenReturn(Optional.empty());

        assertThrows(DataIntegrityException.class, () -> {
            clienteService.buscarCliente("44444444444");
        });
    }

    @Test
    public void testBuscarClienteId_Success() {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(10L);
        entity.setNome("Cliente Id");
        entity.setCpf("55555555555");
        entity.setEmail("cliente@id.com");

        when(clienteRepository.findById(10L)).thenReturn(Optional.of(entity));

        ClienteDTO dto = clienteService.buscarClienteId(10L);

        assertEquals("Cliente Id", dto.getNome());
    }

    @Test
    public void testBuscarClienteId_NotFound() {
        when(clienteRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(DataIntegrityException.class, () -> {
            clienteService.buscarClienteId(20L);
        });
    }

    @Test
    public void testAtualizarCliente_Success() {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(1L);
        dto.setNome("Cliente Atualizado");
        dto.setCpf("66666666666");
        dto.setEmail("atualizado@teste.com");

        ClienteEntity entity = mapper.map(dto, ClienteEntity.class);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(entity);

        ClienteDTO result = clienteService.atualizarCliente(1L, dto);

        assertEquals("Cliente Atualizado", result.getNome());
    }

    @Test
    public void testAtualizarCliente_NotFound() {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(2L);
        dto.setNome("Cliente Atualizado");
        dto.setCpf("77777777777");
        dto.setEmail("atualizado@teste.com");

        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.atualizarCliente(2L, dto);
        });
    }

    @Test
    public void testExcluirCliente_Success() {
        ClienteEntity entity = new ClienteEntity();
        entity.setId(3L);
        entity.setNome("Cliente Excluir");
        entity.setCpf("88888888888");
        entity.setEmail("excluir@teste.com");

        when(clienteRepository.findById(3L)).thenReturn(Optional.of(entity));
        doNothing().when(clienteRepository).delete(any(ClienteEntity.class));

        clienteService.excluirCliente(3L);

        verify(clienteRepository).delete(any(ClienteEntity.class));
    }
}
