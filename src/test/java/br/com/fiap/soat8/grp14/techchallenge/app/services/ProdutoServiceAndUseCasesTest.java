package br.com.fiap.soat8.grp14.techchallenge.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.soat8.grp14.techchallenge.app.dto.produto.ProdutoDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.dto.produto.ProdutoInsertDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.exceptions.DataIntegrityException;
import br.com.fiap.soat8.grp14.techchallenge.app.exceptions.EntityNotFoundException;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.Produto;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.enums.CategoriaProduto;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.AtualizarProdutoUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.BuscarProdutoCategoriaUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.BuscarProdutoIdUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.CriarProdutoUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.ExcluirProdutoUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.ListarProdutosUseCase;
import br.com.fiap.soat8.grp14.techchallenge.data.models.ProdutoEntity;
import br.com.fiap.soat8.grp14.techchallenge.data.repositories.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceAndUseCasesTest {

    // Mocks para os use cases que serão injetados no service
    @Mock
    private CriarProdutoUseCase criarProdutoUseCase;

    @Mock
    private BuscarProdutoCategoriaUseCase buscarProdutoCategoriaUseCase;

    @Mock
    private ListarProdutosUseCase listarProdutosUseCase;

    @Mock
    private BuscarProdutoIdUseCase buscarProdutoIdUseCase;

    @Mock
    private ExcluirProdutoUseCase excluirProdutoUseCase;

    @Mock
    private AtualizarProdutoUseCase atualizarProdutoUseCase;

    // **Utilize o ModelMapper como mock para permitir stubbing**
    @Mock
    private ModelMapper mapper;

    // Serviço a ser testado (usando os use cases mockados)
    private ProdutoService produtoService;

    // --- Para testes dos useCases (instâncias "reais" com repositório mockado) ---
    @Mock
    private ProdutoRepository produtoRepository;

    private CriarProdutoUseCase criarProdutoUseCaseReal;
    private BuscarProdutoCategoriaUseCase buscarProdutoCategoriaUseCaseReal;
    private ListarProdutosUseCase listarProdutosUseCaseReal;
    private BuscarProdutoIdUseCase buscarProdutoIdUseCaseReal;
    private AtualizarProdutoUseCase atualizarProdutoUseCaseReal;
    private ExcluirProdutoUseCase excluirProdutoUseCaseReal;

    @BeforeEach
    public void setUp() {
        // Inicializa o service com os use cases mockados e o ModelMapper mockado
        produtoService = new ProdutoService(
                criarProdutoUseCase,
                buscarProdutoCategoriaUseCase,
                listarProdutosUseCase,
                buscarProdutoIdUseCase,
                excluirProdutoUseCase,
                atualizarProdutoUseCase,
                mapper
        );

        // Instancia os use cases "reais" passando o repositório mockado
        criarProdutoUseCaseReal = new CriarProdutoUseCase(produtoRepository);
        buscarProdutoCategoriaUseCaseReal = new BuscarProdutoCategoriaUseCase(produtoRepository);
        listarProdutosUseCaseReal = new ListarProdutosUseCase(produtoRepository);
        buscarProdutoIdUseCaseReal = new BuscarProdutoIdUseCase(produtoRepository);
        atualizarProdutoUseCaseReal = new AtualizarProdutoUseCase(produtoRepository);
        excluirProdutoUseCaseReal = new ExcluirProdutoUseCase(produtoRepository, buscarProdutoIdUseCaseReal);

        // Injeta o ModelMapper (mockado) em cada use case
        ReflectionTestUtils.setField(criarProdutoUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(buscarProdutoCategoriaUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(listarProdutosUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(buscarProdutoIdUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(atualizarProdutoUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(excluirProdutoUseCaseReal, "modelMapper", mapper);
    }

    // =========================
    // Testes do ProdutoService
    // =========================

    @Test
    public void testBuscarProdutos() {
        // Arrange
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(listarProdutosUseCase.execute(true)).thenReturn(List.of(produto));
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        // Act
        List<ProdutoDTO> result = produtoService.buscarProdutos();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testBuscarPorId() {
        // Arrange
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(buscarProdutoIdUseCase.execute(1L)).thenReturn(produto);
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        // Act
        ProdutoDTO result = produtoService.buscarPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testBuscarPorCategoria() {
        // Arrange
        CategoriaProduto categoria = CategoriaProduto.LANCHE; // ajuste conforme seus enums
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(buscarProdutoCategoriaUseCase.execute(categoria)).thenReturn(List.of(produto));
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        // Act
        List<ProdutoDTO> result = produtoService.buscarPorCategoria(categoria);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testSalvarProduto() {
        // Arrange
        ProdutoInsertDTO insertDTO = new ProdutoInsertDTO();
        // configure insertDTO se necessário

        ProdutoEntity produtoEntity = new ProdutoEntity();
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(mapper.map(insertDTO, ProdutoEntity.class)).thenReturn(produtoEntity);
        when(criarProdutoUseCase.execute(produtoEntity)).thenReturn(produto);
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        // Act
        ProdutoDTO result = produtoService.salvarProduto(insertDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testAtualizarProduto() {
        // Arrange
        Long id = 1L;
        ProdutoDTO inputDTO = new ProdutoDTO();
        inputDTO.setId(id);
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(id);
        Produto produto = new Produto();
        produto.setId(id);
        ProdutoDTO outputDTO = new ProdutoDTO();
        outputDTO.setId(id);

        when(mapper.map(inputDTO, ProdutoEntity.class)).thenReturn(produtoEntity);
        when(atualizarProdutoUseCase.execute(produtoEntity)).thenReturn(produto);
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(outputDTO);

        // Act
        ProdutoDTO result = produtoService.atualizarProduto(id, inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void testDeletarProduto() {
        // Arrange
        Long id = 1L;
        // Como o método execute() retorna Boolean, simulamos o retorno true:
        when(excluirProdutoUseCase.execute(id)).thenReturn(true);

        // Act
        produtoService.deletarProduto(id);

        // Assert
        verify(excluirProdutoUseCase, times(1)).execute(id);
    }


    @Test
    public void testAtualizarProdutoUseCase_Success() {
        // Arrange
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoEntity));
        when(produtoRepository.save(produtoEntity)).thenReturn(produtoEntity);

        // Stub para o ModelMapper: converte o ProdutoEntity (salvo) em um Produto
        Produto produto = new Produto();
        produto.setId(produtoEntity.getId());
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        // Act
        Produto result = atualizarProdutoUseCaseReal.execute(produtoEntity);

        // Assert
        assertNotNull(result);
        assertEquals(produtoEntity.getId(), result.getId());
    }


    @Test
    public void testAtualizarProdutoUseCase_NotFound() {
        // Arrange
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> atualizarProdutoUseCaseReal.execute(produtoEntity));
    }

    @Test
    public void testBuscarProdutoCategoriaUseCase() {
        // Arrange
        CategoriaProduto categoria = CategoriaProduto.LANCHE;
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findByCategoriaProduto(categoria)).thenReturn(List.of(produtoEntity));

        // Act
        List<Produto> result = buscarProdutoCategoriaUseCaseReal.execute(categoria);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarProdutoIdUseCase_Success() {
        // Arrange
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoEntity));

        // Cria o objeto Produto que deve ser retornado pelo mapper
        Produto produto = new Produto();
        produto.setId(1L);
        // Stub para o ModelMapper: converte ProdutoEntity para Produto
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        // Act
        Produto result = buscarProdutoIdUseCaseReal.execute(1L);

        // Assert
        assertNotNull(result);
        assertEquals(produtoEntity.getId(), result.getId());
    }


    @Test
    public void testBuscarProdutoIdUseCase_NotFound() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataIntegrityException.class, () -> buscarProdutoIdUseCaseReal.execute(1L));
    }

    @Test
    public void testCriarProdutoUseCase() {
        // Arrange
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.save(produtoEntity)).thenReturn(produtoEntity);

        // Cria um objeto Produto para ser retornado pelo mapper
        Produto produto = new Produto();
        produto.setId(1L);

        // Stub para o mapper, para que ele converta ProdutoEntity para Produto
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        // Act
        Produto result = criarProdutoUseCaseReal.execute(produtoEntity);

        // Assert
        assertNotNull(result);
        assertEquals(produtoEntity.getId(), result.getId());
    }


    @Test
    public void testExcluirProdutoUseCase() {
        // Arrange
        Long id = 1L;
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(id);

        // Cria o objeto Produto correspondente
        Produto produto = new Produto();
        produto.setId(id);

        // Stub: Simula que o repositório encontra o produtoEntity pelo id
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoEntity));

        // Stub: Configura o mapper para converter ProdutoEntity -> Produto
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);
        // Stub: Configura o mapper para converter Produto -> ProdutoEntity
        when(mapper.map(produto, ProdutoEntity.class)).thenReturn(produtoEntity);

        // Stub para o método delete (método void)
        doNothing().when(produtoRepository).delete(any(ProdutoEntity.class));

        // Act
        Boolean result = excluirProdutoUseCaseReal.execute(id);

        // Assert
        assertTrue(result);
        // Verifica que o método delete foi chamado com o produtoEntity correto
        verify(produtoRepository, times(1)).delete(produtoEntity);
    }


    @Test
    public void testListarProdutosUseCase() {

        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findAll()).thenReturn(List.of(produtoEntity));

        Produto produto = new Produto();
        produto.setId(produtoEntity.getId());
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        List<Produto> result = listarProdutosUseCaseReal.execute(true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(produtoEntity.getId(), result.get(0).getId());
    }

}
