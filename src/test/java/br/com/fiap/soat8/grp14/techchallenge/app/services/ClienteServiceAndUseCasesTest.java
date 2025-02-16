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

    // --- Mock do repositório ---
    @Mock
    private ClienteRepository clienteRepository;

    // Usaremos uma instância real do ModelMapper para facilitar o mapeamento
    private ModelMapper mapper = new ModelMapper();

    // --- Instâncias dos use cases ---
    private CriarClienteUseCase criarClienteUseCase;
    private BuscarClienteCpfUseCase buscarClienteCpfUseCase;
    private BuscarClienteIdUseCase buscarClienteIdUseCase;
    private AtualizarClienteUseCase atualizarClienteUseCase;
    private ExcluirClienteUseCase excluirClienteUseCase;
    private ListarClienteUseCase listarClienteUseCase;

    // --- Instância da Service ---
    private ClienteService clienteService;

    // Método auxiliar para injetar o ModelMapper via reflexão
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
        // Inicializa os use cases com o repository mockado
        criarClienteUseCase = new CriarClienteUseCase(clienteRepository);
        buscarClienteCpfUseCase = new BuscarClienteCpfUseCase(clienteRepository);
        buscarClienteIdUseCase = new BuscarClienteIdUseCase(clienteRepository);
        atualizarClienteUseCase = new AtualizarClienteUseCase(clienteRepository);
        listarClienteUseCase = new ListarClienteUseCase(clienteRepository);
        excluirClienteUseCase = new ExcluirClienteUseCase(clienteRepository, buscarClienteIdUseCase);

        // Injetar o ModelMapper em todos os use cases via reflexão
        injectModelMapper(criarClienteUseCase, mapper);
        injectModelMapper(buscarClienteCpfUseCase, mapper);
        injectModelMapper(buscarClienteIdUseCase, mapper);
        injectModelMapper(atualizarClienteUseCase, mapper);
        injectModelMapper(listarClienteUseCase, mapper);
        injectModelMapper(excluirClienteUseCase, mapper);

        // Inicializa a service com os use cases
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

    // ===== Testes para CriarClienteUseCase (salvarCliente) =====
    @Test
    public void testSalvarCliente_Success() {
        // Arrange
        ClienteInsertDTO insertDTO = new ClienteInsertDTO();
        insertDTO.setNome("Teste");
        insertDTO.setCpf("11111111111");
        insertDTO.setEmail("teste@teste.com");

        ClienteEntity entity = mapper.map(insertDTO, ClienteEntity.class);
        // Simula que nenhum cliente com esse CPF existe
        when(clienteRepository.findByCpf("11111111111")).thenReturn(Optional.empty());
        // Simula o salvamento no repository
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(entity);

        // Act
        ClienteDTO result = clienteService.salvarCliente(insertDTO);

        // Assert
        assertEquals("Teste", result.getNome());
        assertEquals("11111111111", result.getCpf());
        assertEquals("teste@teste.com", result.getEmail());
    }

    @Test
    public void testSalvarCliente_DuplicateCpf() {
        // Arrange
        ClienteInsertDTO insertDTO = new ClienteInsertDTO();
        insertDTO.setNome("Teste");
        insertDTO.setCpf("22222222222");
        insertDTO.setEmail("teste@teste.com");

        ClienteEntity existingEntity = new ClienteEntity();
        existingEntity.setCpf("22222222222");

        // Simula que já existe um cliente com esse CPF
        when(clienteRepository.findByCpf("22222222222")).thenReturn(Optional.of(existingEntity));

        // Act & Assert
        assertThrows(DataIntegrityException.class, () -> {
            clienteService.salvarCliente(insertDTO);
        });
    }

    // ===== Testes para ListarClienteUseCase (listarTodos) =====
    @Test
    public void testListarTodos_WithData() {
        // Arrange
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

        // Act
        List<ClienteDTO> list = clienteService.listarTodos();

        // Assert
        assertEquals(2, list.size());
        assertEquals("Cliente A", list.get(0).getNome());
        assertEquals("Cliente B", list.get(1).getNome());
    }

    @Test
    public void testListarTodos_Empty() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ClienteDTO> list = clienteService.listarTodos();

        // Assert
        assertTrue(list.isEmpty());
    }

    // ===== Testes para BuscarClienteCpfUseCase (buscarCliente) =====
    @Test
    public void testBuscarClienteCpf_Success() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId(1L);
        entity.setNome("Cliente Teste");
        entity.setCpf("33333333333");
        entity.setEmail("cliente@teste.com");

        when(clienteRepository.findByCpf("33333333333")).thenReturn(Optional.of(entity));

        // Act
        ClienteDTO dto = clienteService.buscarCliente("33333333333");

        // Assert
        assertEquals("Cliente Teste", dto.getNome());
    }

    @Test
    public void testBuscarClienteCpf_NotFound() {
        // Arrange
        when(clienteRepository.findByCpf("44444444444")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataIntegrityException.class, () -> {
            clienteService.buscarCliente("44444444444");
        });
    }

    // ===== Testes para BuscarClienteIdUseCase (buscarClienteId) =====
    @Test
    public void testBuscarClienteId_Success() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId(10L);
        entity.setNome("Cliente Id");
        entity.setCpf("55555555555");
        entity.setEmail("cliente@id.com");

        when(clienteRepository.findById(10L)).thenReturn(Optional.of(entity));

        // Act
        ClienteDTO dto = clienteService.buscarClienteId(10L);

        // Assert
        assertEquals("Cliente Id", dto.getNome());
    }

    @Test
    public void testBuscarClienteId_NotFound() {
        // Arrange
        when(clienteRepository.findById(20L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataIntegrityException.class, () -> {
            clienteService.buscarClienteId(20L);
        });
    }

    // ===== Testes para AtualizarClienteUseCase (atualizarCliente) =====
    @Test
    public void testAtualizarCliente_Success() {
        // Arrange
        ClienteDTO dto = new ClienteDTO();
        dto.setId(1L);
        dto.setNome("Cliente Atualizado");
        dto.setCpf("66666666666");
        dto.setEmail("atualizado@teste.com");

        ClienteEntity entity = mapper.map(dto, ClienteEntity.class);
        // Simula que o cliente existe para a atualização
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(entity));
        // Simula o salvamento da atualização
        when(clienteRepository.save(any(ClienteEntity.class))).thenReturn(entity);

        // Act
        ClienteDTO result = clienteService.atualizarCliente(1L, dto);

        // Assert
        assertEquals("Cliente Atualizado", result.getNome());
    }

    @Test
    public void testAtualizarCliente_NotFound() {
        // Arrange
        ClienteDTO dto = new ClienteDTO();
        dto.setId(2L);
        dto.setNome("Cliente Atualizado");
        dto.setCpf("77777777777");
        dto.setEmail("atualizado@teste.com");

        // Simula que o cliente não existe
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.atualizarCliente(2L, dto);
        });
    }

    // ===== Testes para ExcluirClienteUseCase (excluirCliente) =====
    @Test
    public void testExcluirCliente_Success() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId(3L);
        entity.setNome("Cliente Excluir");
        entity.setCpf("88888888888");
        entity.setEmail("excluir@teste.com");

        // Simula que o cliente existe (usado pelo use case de buscar por ID)
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(entity));
        // Para o delete, apenas garante que o método será chamado
        doNothing().when(clienteRepository).delete(any(ClienteEntity.class));

        // Act
        clienteService.excluirCliente(3L);

        // Assert: verifica se o método delete foi chamado
        verify(clienteRepository).delete(any(ClienteEntity.class));
    }
}
