package br.com.fiap.soat8.grp14.techchallenge.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import br.com.fiap.soat8.grp14.techchallenge.app.dto.produto.ProdutoDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.dto.produto.ProdutoInsertDTO;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.Produto;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.enums.CategoriaProduto;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.AtualizarProdutoUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.BuscarProdutoCategoriaUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.BuscarProdutoIdUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.CriarProdutoUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.ExcluirProdutoUseCase;
import br.com.fiap.soat8.grp14.techchallenge.core.usecases.produto.ListarProdutosUseCase;

public class ProdutoServiceTest {

    private ProdutoService produtoService;
    private CriarProdutoUseCase criarProdutoUseCase;
    private BuscarProdutoCategoriaUseCase buscarProdutoCategoriaUseCase;
    private ListarProdutosUseCase listarProdutosUseCase;
    private BuscarProdutoIdUseCase buscarProdutoIdUseCase;
    private ExcluirProdutoUseCase excluirProdutoUseCase;
    private AtualizarProdutoUseCase atualizarProdutoUseCase;
    private ModelMapper mapper;

    @BeforeEach
    public void setUp() {
        criarProdutoUseCase = Mockito.mock(CriarProdutoUseCase.class);
        buscarProdutoCategoriaUseCase = Mockito.mock(BuscarProdutoCategoriaUseCase.class);
        listarProdutosUseCase = Mockito.mock(ListarProdutosUseCase.class);
        buscarProdutoIdUseCase = Mockito.mock(BuscarProdutoIdUseCase.class);
        excluirProdutoUseCase = Mockito.mock(ExcluirProdutoUseCase.class);
        atualizarProdutoUseCase = Mockito.mock(AtualizarProdutoUseCase.class);
        mapper = new ModelMapper();
        produtoService = new ProdutoService(criarProdutoUseCase, buscarProdutoCategoriaUseCase, listarProdutosUseCase, buscarProdutoIdUseCase, excluirProdutoUseCase, atualizarProdutoUseCase, mapper);
    }

    @Test
    public void testSalvarProduto() {
        ProdutoInsertDTO produtoInsertDTO = new ProdutoInsertDTO();
        produtoInsertDTO.setNome("Produto Teste");
        produtoInsertDTO.setDescricao("Descrição do Produto Teste");
        produtoInsertDTO.setCategoriaProduto(CategoriaProduto.ACOMPANHAMENTO);
        produtoInsertDTO.setValor(100.0);

        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Produto Teste");
        produtoDTO.setDescricao("Descrição do Produto Teste");
        produtoDTO.setCategoriaProduto(CategoriaProduto.ACOMPANHAMENTO);
        produtoDTO.setValor(100.0);

        when(criarProdutoUseCase.execute(any())).thenReturn(mapper.map(produtoInsertDTO, Produto.class));

        ProdutoDTO result = produtoService.salvarProduto(produtoInsertDTO);
        assertEquals("Produto Teste", result.getNome());
    }

    @Test
    public void testBuscarProdutos() {
        when(listarProdutosUseCase.execute(true)).thenReturn(Collections.emptyList());

        List<ProdutoDTO> result = produtoService.buscarProdutos();
        assertEquals(0, result.size());
    }

    @Test
    public void testBuscarPorId() {
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Produto Teste");

        when(buscarProdutoIdUseCase.execute(anyLong())).thenReturn(mapper.map(produtoDTO, Produto.class));

        ProdutoDTO result = produtoService.buscarPorId(1L);
        assertEquals("Produto Teste", result.getNome());
    }

    @Test
    public void testBuscarPorCategoria() {
        when(buscarProdutoCategoriaUseCase.execute(any(CategoriaProduto.class))).thenReturn(Collections.emptyList());

        List<ProdutoDTO> result = produtoService.buscarPorCategoria(CategoriaProduto.ACOMPANHAMENTO);
        assertEquals(0, result.size());
    }

    @Test
    public void testAtualizarProduto() {
        ProdutoDTO produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Produto Atualizado");
        produtoDTO.setDescricao("Descrição do Produto Atualizado");
        produtoDTO.setCategoriaProduto(CategoriaProduto.ACOMPANHAMENTO);
        produtoDTO.setValor(150.0);

        when(atualizarProdutoUseCase.execute(any())).thenReturn(mapper.map(produtoDTO, Produto.class));

        ProdutoDTO result = produtoService.atualizarProduto(1L, produtoDTO);
        assertEquals("Produto Atualizado", result.getNome());
    }

    @Test
    public void testDeletarProduto() {
        produtoService.deletarProduto(1L);
        Mockito.verify(excluirProdutoUseCase).execute(1L);
    }
}