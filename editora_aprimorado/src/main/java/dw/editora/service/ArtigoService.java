package dw.editora.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dw.editora.entity.Artigo;
import dw.editora.repository.ArtigoRepository;
// listar os artigos por autor
import dw.editora.repository.AutorRepository;

import dw.editora.entity.Autor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// @Service diz ao Spring que esta classe contém a lógica de negócio
@Service
public class ArtigoService {

    private final ArtigoRepository repository;
    // listar os artigos por autor
    private final AutorRepository autorRepository;

    public ArtigoService(
        ArtigoRepository repository,
        AutorRepository autorRepository) 
    {
        this.repository = repository;
        this.autorRepository = autorRepository;
    }

    public List<Artigo> listarArtigos(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return repository.findAll();
        }
        return repository.findByTituloContainingIgnoreCase(titulo);
    }

    public Optional<Artigo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Artigo salvar(Artigo artigo) {
        if (artigo.getAutor() == null || artigo.getAutor().getId() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Informe o ID do autor."
            );
        }

        Autor autor = autorRepository.findById(artigo.getAutor().getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Autor não encontrado."
            ));

        artigo.setAutor(autor);

        return repository.save(artigo);
    }

    public Optional<Artigo> atualizar(Long id, Artigo artigoAtualizado) {
        return repository.findById(id).map(artigo -> {
            artigo.setTitulo(artigoAtualizado.getTitulo());
            artigo.setResumo(artigoAtualizado.getResumo());
            artigo.setPublicado(artigoAtualizado.isPublicado());
            return repository.save(artigo);
        });
    }

    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public void deletarTodos() {
        repository.deleteAll();
    }

    public List<Artigo> listarPublicados() {
        return repository.findByPublicado(true);
    }

    public List<Artigo> listarPorAutor(Long autorId) {
        if (!autorRepository.existsById(autorId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Autor não encontrado."
            );
        }

        return repository.findByAutorId(autorId);
    }

    
}