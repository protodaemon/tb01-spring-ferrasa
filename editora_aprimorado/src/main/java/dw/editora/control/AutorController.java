package dw.editora.control;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dw.editora.service.ArtigoService;
import dw.editora.entity.Artigo;

import dw.editora.entity.Autor;
import dw.editora.service.AutorService;

@CrossOrigin(origins = "*") 
@RestController 
@RequestMapping("/api/autores") // Mapeia o recurso na raiz da classe
public class AutorController 
{
    // A dependência agora é do Service, não do Repository
    private final AutorService service;
    private final ArtigoService artigoService;

    public AutorController(AutorService service, ArtigoService artigoService) {
        this.service = service;
        this.artigoService = artigoService;
    }

    @GetMapping
    public ResponseEntity<List<Autor>> getAllAutores(@RequestParam(required = false) String nome) {
        try {
            List<Autor> autores = service.listarAutores(nome);
            
            if (autores.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(autores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> getAutor(@PathVariable Long id) {
        // Uso de programação funcional (map) para simplificar o Optional
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Autor> createAutor(@RequestBody Autor autor) {
        try {
            Autor autorSalvo = service.salvar(autor);
            return ResponseEntity.status(HttpStatus.CREATED).body(autorSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/artigos")
    public ResponseEntity<List<Artigo>> getArtigosPorAutor(@PathVariable Long id) {
        List<Artigo> artigos = artigoService.listarPorAutor(id);

        if (artigos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(artigos);
    }


}