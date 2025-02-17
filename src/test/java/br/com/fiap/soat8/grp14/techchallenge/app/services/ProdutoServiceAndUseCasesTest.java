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

    @Mock
    private ModelMapper mapper;

    private ProdutoService produtoService;

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
        produtoService = new ProdutoService(
                criarProdutoUseCase,
                buscarProdutoCategoriaUseCase,
                listarProdutosUseCase,
                buscarProdutoIdUseCase,
                excluirProdutoUseCase,
                atualizarProdutoUseCase,
                mapper
        );

        criarProdutoUseCaseReal = new CriarProdutoUseCase(produtoRepository);
        buscarProdutoCategoriaUseCaseReal = new BuscarProdutoCategoriaUseCase(produtoRepository);
        listarProdutosUseCaseReal = new ListarProdutosUseCase(produtoRepository);
        buscarProdutoIdUseCaseReal = new BuscarProdutoIdUseCase(produtoRepository);
        atualizarProdutoUseCaseReal = new AtualizarProdutoUseCase(produtoRepository);
        excluirProdutoUseCaseReal = new ExcluirProdutoUseCase(produtoRepository, buscarProdutoIdUseCaseReal);

        ReflectionTestUtils.setField(criarProdutoUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(buscarProdutoCategoriaUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(listarProdutosUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(buscarProdutoIdUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(atualizarProdutoUseCaseReal, "modelMapper", mapper);
        ReflectionTestUtils.setField(excluirProdutoUseCaseReal, "modelMapper", mapper);
    }

    @Test
    public void testBuscarProdutos() {
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(listarProdutosUseCase.execute(true)).thenReturn(List.of(produto));
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        List<ProdutoDTO> result = produtoService.buscarProdutos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testBuscarPorId() {
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(buscarProdutoIdUseCase.execute(1L)).thenReturn(produto);
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        ProdutoDTO result = produtoService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testBuscarPorCategoria() {
        CategoriaProduto categoria = CategoriaProduto.LANCHE;
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(buscarProdutoCategoriaUseCase.execute(categoria)).thenReturn(List.of(produto));
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        List<ProdutoDTO> result = produtoService.buscarPorCategoria(categoria);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void testSalvarProduto() {
        ProdutoInsertDTO insertDTO = new ProdutoInsertDTO();

        ProdutoEntity produtoEntity = new ProdutoEntity();
        Produto produto = new Produto();
        produto.setId(1L);
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setId(1L);

        when(mapper.map(insertDTO, ProdutoEntity.class)).thenReturn(produtoEntity);
        when(criarProdutoUseCase.execute(produtoEntity)).thenReturn(produto);
        when(mapper.map(produto, ProdutoDTO.class)).thenReturn(produtoDTO);

        ProdutoDTO result = produtoService.salvarProduto(insertDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testAtualizarProduto() {
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

        ProdutoDTO result = produtoService.atualizarProduto(id, inputDTO);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void testDeletarProduto() {
        Long id = 1L;
        when(excluirProdutoUseCase.execute(id)).thenReturn(true);

        produtoService.deletarProduto(id);

        verify(excluirProdutoUseCase, times(1)).execute(id);
    }


    @Test
    public void testAtualizarProdutoUseCase_Success() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoEntity));
        when(produtoRepository.save(produtoEntity)).thenReturn(produtoEntity);

        Produto produto = new Produto();
        produto.setId(produtoEntity.getId());
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        Produto result = atualizarProdutoUseCaseReal.execute(produtoEntity);

        assertNotNull(result);
        assertEquals(produtoEntity.getId(), result.getId());
    }


    @Test
    public void testAtualizarProdutoUseCase_NotFound() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> atualizarProdutoUseCaseReal.execute(produtoEntity));
    }

    @Test
    public void testBuscarProdutoCategoriaUseCase() {
        CategoriaProduto categoria = CategoriaProduto.LANCHE;
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findByCategoriaProduto(categoria)).thenReturn(List.of(produtoEntity));

        List<Produto> result = buscarProdutoCategoriaUseCaseReal.execute(categoria);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testBuscarProdutoIdUseCase_Success() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoEntity));

        Produto produto = new Produto();
        produto.setId(1L);
        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        Produto result = buscarProdutoIdUseCaseReal.execute(1L);

        assertNotNull(result);
        assertEquals(produtoEntity.getId(), result.getId());
    }


    @Test
    public void testBuscarProdutoIdUseCase_NotFound() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataIntegrityException.class, () -> buscarProdutoIdUseCaseReal.execute(1L));
    }

    @Test
    public void testCriarProdutoUseCase() {
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(1L);

        when(produtoRepository.save(produtoEntity)).thenReturn(produtoEntity);

        Produto produto = new Produto();
        produto.setId(1L);

        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);

        Produto result = criarProdutoUseCaseReal.execute(produtoEntity);

        assertNotNull(result);
        assertEquals(produtoEntity.getId(), result.getId());
    }


    @Test
    public void testExcluirProdutoUseCase() {
        Long id = 1L;
        ProdutoEntity produtoEntity = new ProdutoEntity();
        produtoEntity.setId(id);

        Produto produto = new Produto();
        produto.setId(id);

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoEntity));

        when(mapper.map(produtoEntity, Produto.class)).thenReturn(produto);
        when(mapper.map(produto, ProdutoEntity.class)).thenReturn(produtoEntity);

        doNothing().when(produtoRepository).delete(any(ProdutoEntity.class));

        Boolean result = excluirProdutoUseCaseReal.execute(id);

        assertTrue(result);
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
