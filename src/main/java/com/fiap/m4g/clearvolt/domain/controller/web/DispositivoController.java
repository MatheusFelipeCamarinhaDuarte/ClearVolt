package com.fiap.m4g.clearvolt.domain.controller.web;

import com.fiap.m4g.clearvolt.domain.entity.ConfiguracaoDeColeta;
import com.fiap.m4g.clearvolt.domain.entity.DadoColetado;
import com.fiap.m4g.clearvolt.domain.entity.Dispositivo;
import com.fiap.m4g.clearvolt.domain.entity.Usuario;
import com.fiap.m4g.clearvolt.domain.repository.ConfiguracaoDeColetaRepository;
import com.fiap.m4g.clearvolt.domain.repository.DadoColetadoRepository;
import com.fiap.m4g.clearvolt.domain.repository.DispositivoRepository;
import com.fiap.m4g.clearvolt.domain.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class DispositivoController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private DadoColetadoRepository dadoColetadoRepository;
    @Autowired
    private ConfiguracaoDeColetaRepository configuracaoDeColetaRepository;

    private Authentication getAuthentication() { return SecurityContextHolder.getContext().getAuthentication(); }
    private boolean autenticado() { return getAuthentication() != null && getAuthentication().isAuthenticated() && !(getAuthentication().getPrincipal() instanceof String && "anonymousUser".equals(getAuthentication().getPrincipal())); }


    // Essa rota leva para a página de dispositivos
    @GetMapping("/dispositivos")
    public ModelAndView pageDispositivos() {
        ModelAndView mv = new ModelAndView("dispositivos");
        mv.addObject("autenticado", autenticado());
        Authentication authentication = getAuthentication();
        Optional<Usuario> usuario = usuarioRepository.findByEmail(authentication.getName());
        if (usuario.isPresent()){
            List<Dispositivo> dispositivos = dispositivoRepository.findAllByDono(usuario);
            if (!dispositivos.isEmpty()) {
                mv.addObject("dispositivos", dispositivos);
            }
        }
        return mv;
    }

    // Está rota leva a página de adição de dispositivos
    @GetMapping("/dispositivos/novo")
    public ModelAndView pageNovoDispositivo(){
        ModelAndView mv = new ModelAndView("novo_dispositivo");
        mv.addObject("autenticado", autenticado());
        mv.addObject("dispositivo", new Dispositivo());
        Authentication authentication = getAuthentication();
        Usuario usuarioMaster = usuarioRepository.findById(1L).orElseThrow();
        Usuario usuarioAtual = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        List<ConfiguracaoDeColeta> listaConfigs = configuracaoDeColetaRepository.findAllByUsuarioPertencente(usuarioMaster);
        if (!usuarioMaster.equals(usuarioAtual)){
            List<ConfiguracaoDeColeta> listaConfigsDoUsuario = configuracaoDeColetaRepository.findAllByUsuarioPertencente(usuarioAtual);
            listaConfigs.addAll(listaConfigsDoUsuario);
        }
        mv.addObject("lista_configs", listaConfigs);
        return mv;
    }

    // Este rota salva o dispositivo no banco
    @PostMapping("/dispositivos/salvar")
    public ModelAndView salvarDispositivo(@Valid Dispositivo dispositivo, BindingResult bd){
        if (dispositivoRepository.findByIdentificador(dispositivo.getIdentificador()).isPresent()){
            ModelAndView mv =  new ModelAndView("redirect:/dispositivos/novo");
            mv.addObject("errorMessage","Identificador do dispositivo já cadastrado");
            mv.addObject("autenticado", autenticado());
            return mv;
        }
        if (bd.hasErrors()){
            ModelAndView mv =  new ModelAndView("novo_dispositivo");
            mv.addObject("autenticado",autenticado());
            mv.addObject("dispositivo",dispositivo);
            Authentication authentication = getAuthentication();
            Usuario usuarioMaster = usuarioRepository.findById(1L).orElseThrow();
            Usuario usuarioAtual = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
            List<ConfiguracaoDeColeta> listaConfigs = configuracaoDeColetaRepository.findAllByUsuarioPertencente(usuarioMaster);
            if (!usuarioMaster.equals(usuarioAtual)){
                List<ConfiguracaoDeColeta> listaConfigsDoUsuario = configuracaoDeColetaRepository.findAllByUsuarioPertencente(usuarioAtual);
                listaConfigs.addAll(listaConfigsDoUsuario);
            }
            mv.addObject("lista_configs", listaConfigs);
            System.out.println(dispositivo.getConfigAtual());
            return mv;
        } else {
            Authentication authentication = getAuthentication();
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
            dispositivo.setDono(usuario);
            ConfiguracaoDeColeta configAtual = configuracaoDeColetaRepository.findById(dispositivo.getConfigAtual().getId()).orElseThrow();
            dispositivo.setConfigAtual(configAtual);
            dispositivoRepository.save(dispositivo);
            ModelAndView mv = new ModelAndView("redirect:/dispositivos");
            return mv;

        }

    }

    // Está rota leva para a página de atualização do dispositivo
    @GetMapping("/dispositivo/atualizar")
    public ModelAndView pageAtualizarDispositivo(){
        ModelAndView mv = new ModelAndView("atualizar_dispositivo");

        return mv;
    }

    // Está rota atualiza as informações de determinado dispositivo no banco de dados
    @PostMapping("/dispositivo/alterar")
    public ModelAndView alterarDispositivo(){
        ModelAndView mv = new ModelAndView("");

        return mv;
    }

    // Está rota exclui o dispositivo do banco de dados
    @GetMapping("/dispositivo/excluir/{id}")
    public ModelAndView excluirDispositivo(@PathVariable Long id){
        Optional<Dispositivo> dispositivo = dispositivoRepository.findById(id);
        Authentication authentication = getAuthentication();
        Usuario usuarioAtual = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        if (dispositivo.isPresent()){
            if (dispositivo.get().getDono().equals(usuarioAtual)) {
                dadoColetadoRepository.deleteAllByDispositivoPertencente(dispositivo.get());
                dispositivoRepository.deleteById(id);
                return new ModelAndView("redirect:/dispositivos");
            } else {
                return new ModelAndView("redirect:/acesso_negado");
            }
        }
        return new ModelAndView("redirect:/dispositivos");

    }


}
