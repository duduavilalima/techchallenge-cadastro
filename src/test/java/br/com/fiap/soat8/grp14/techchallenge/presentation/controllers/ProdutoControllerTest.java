package br.com.fiap.soat8.grp14.techchallenge.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import br.com.fiap.soat8.grp14.techchallenge.app.dto.produto.ProdutoDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.dto.produto.ProdutoInsertDTO;
import br.com.fiap.soat8.grp14.techchallenge.app.services.ProdutoService;
import br.com.fiap.soat8.grp14.techchallenge.core.entities.enums.CategoriaProduto;

public class ProdutoControllerTest {

	private MockMvc mockMvc;
	private ProdutoService produtoService;
	private ObjectMapper objectMapper;
	
	@BeforeEach
	public void setUp() {
		produtoService = Mockito.mock(ProdutoService.class);
		ProdutoController produtoController = new ProdutoController(produtoService);
		mockMvc = MockMvcBuilders.standaloneSetup(produtoController).build();
		objectMapper = new ObjectMapper();
	}
	
	@Test
	public void testSalvarProdutos() throws Exception {
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
		
		when(produtoService.salvarProduto(any(ProdutoInsertDTO.class))).thenReturn(produtoDTO);
		
		mockMvc.perform(post("/api/produtos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(produtoInsertDTO)))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void testGetProdutos() throws Exception {
		when(produtoService.buscarProdutos()).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get("/api/produtos"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testGetProdutoPorId() throws Exception {
		ProdutoDTO produtoDTO = new ProdutoDTO();
		
		when(produtoService.buscarPorId(anyLong())).thenReturn(produtoDTO);
		
		mockMvc.perform(get("/api/produtos/{id}", 1L))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testGetProdutosPorCategoria() throws Exception {
		when(produtoService.buscarPorCategoria(any(CategoriaProduto.class))).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get("/api/produtos/categoria/{categoriaProduto}", CategoriaProduto.ACOMPANHAMENTO))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testAtualizarProdutos() throws Exception {
		ProdutoDTO produtoDTO = new ProdutoDTO();
		produtoDTO.setNome("Produto Atualizado");
		produtoDTO.setDescricao("Descrição do Produto Atualizado");
		produtoDTO.setCategoriaProduto(CategoriaProduto.ACOMPANHAMENTO);
		produtoDTO.setValor(150.0);
		
		when(produtoService.atualizarProduto(anyLong(), any(ProdutoDTO.class))).thenReturn(produtoDTO);
		
		mockMvc.perform(put("/api/produtos/{id}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(produtoDTO)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testDeletaProdutos() throws Exception {
		mockMvc.perform(delete("/api/produtos/{id}", 1L))
				.andExpect(status().isNoContent());
	}
}