package com.fiap.m4g.clearvolt.domain.controller.web;

import com.fiap.m4g.clearvolt.domain.entity.*;
import com.fiap.m4g.clearvolt.domain.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DispositivoRepository dispositivoRepository;
    @Autowired
    private DadoColetadoRepository dadoColetadoRepository;
    @Autowired
    private ConfiguracaoDeColetaRepository configuracaoDeColetaRepository;

    private Authentication getAuthentication() { return SecurityContextHolder.getContext().getAuthentication(); }
    private boolean autenticado() { return getAuthentication() != null && getAuthentication().isAuthenticated() && !(getAuthentication().getPrincipal() instanceof String && "anonymousUser".equals(getAuthentication().getPrincipal())); }
    private String formataTelefone(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            throw new IllegalArgumentException("O telefone não pode ser nulo ou vazio.");
        }
        if (telefone.length() != 11 || !telefone.matches("\\d+")) {
            throw new IllegalArgumentException("O telefone deve conter exatamente 11 dígitos numéricos.");
        }
        String ddd = telefone.substring(0, 2);
        String parte1 = telefone.substring(2, 7);
        String parte2 = telefone.substring(7);
        return String.format("(%s) %s-%s", ddd, parte1, parte2);
    }
    private String formataCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser nulo ou vazio.");
        }
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        String parte1 = cpf.substring(0, 3);
        String parte2 = cpf.substring(3, 6);
        String parte3 = cpf.substring(6,9);
        String parte4 = cpf.substring(9);
        return String.format("%s.%s.%s-%s", parte1, parte2, parte3, parte4);
    }
    private String formataData(LocalDate data) {
        if (data == null || data.toString().isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser nulo ou vazio.");
        }
        if (data.toString().length() != 10) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        Integer ano = data.getYear();
        Integer mes = data.getMonthValue();
        Integer dia = data.getDayOfMonth();

        return String.format("%s/%s/%s", dia, mes, ano);
    }

    @GetMapping("/login")
    public ModelAndView pageLogin() {
        if (autenticado()) {
            return new ModelAndView("redirect:/perfil");
        }
        else { return new ModelAndView("login"); }
    }

    @GetMapping("/cadastro")
    public ModelAndView pageCadastro() {
        ModelAndView mv = new ModelAndView("cadastro");
        mv.addObject("autenticado", autenticado());
        mv.addObject("usuario", new Usuario());
        return mv;
    }

    @PostMapping("/cadastrar")
    public ModelAndView cadastrarUsuario(@Valid Usuario usuario, BindingResult bd) {
        if(usuarioRepository.findByEmail(usuario.getEmail()).isPresent()){
            ModelAndView mv =  new ModelAndView("cadastro");
            mv.addObject("errorMessage","Email já cadastrado");
            mv.addObject("autenticado", autenticado());
            return mv;
        }
        if (bd.hasErrors()) {
            ModelAndView mv =  new ModelAndView("cadastro");
            mv.addObject("autenticado",autenticado());
            mv.addObject("usuario",usuario);
            System.out.println(usuario.getPessoa().getDataDeNascimento());
            return mv;

        } else {
            System.out.println("CADASTRADO");
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Role role = roleRepository.findByName("ROLE_FREE");
            usuario.setUserRole(role);
            usuario.getPessoa().setTelefone(usuario.getPessoa().getTelefone().replaceAll("\\D", ""));
            usuario.getPessoa().setCpf(usuario.getPessoa().getCpf().replaceAll("\\D",""));
            usuarioRepository.save(usuario);
            SecurityContextHolder.getContext().setAuthentication(null);
            ModelAndView mv = new ModelAndView("redirect:/login");
            return mv;

        }
    }

    @GetMapping("/perfil")
    public ModelAndView pagePerfil() {
        if (!autenticado()) {
            return new ModelAndView("redirect:/login");
        }


        ModelAndView mv = new ModelAndView("perfil");
        Authentication authentication = getAuthentication();
        String email = authentication.getName();
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            usuario.get().getPessoa().setTelefone(formataTelefone(usuario.get().getPessoa().getTelefone()));
            usuario.get().getPessoa().setCpf(formataCpf(usuario.get().getPessoa().getCpf()));
            mv.addObject("usuario", usuario.get());
            mv.addObject("nascimento", usuario.get().getPessoa().getDataDeNascimento().toString().replaceAll("-","/"));
            List<Dispositivo> dispositivos = dispositivoRepository.findAllByDono(usuario);
            if (!dispositivos.isEmpty()) {
                mv.addObject("dispositivos", dispositivos);
            }

        }
        mv.addObject("autenticado", autenticado());

        return mv;
    }




    @GetMapping("/perfil/excluir")
    public ModelAndView excluirDispositivo(){
        Authentication authentication = getAuthentication();
        Optional<Usuario> usuarioAtual = usuarioRepository.findByEmail(authentication.getName());
        if (usuarioAtual.isPresent()) {
            List<Dispositivo> dispositivos = dispositivoRepository.findAllByDono(usuarioAtual);
            for (Dispositivo dispositivo : dispositivos) {
                dadoColetadoRepository.deleteAllByDispositivoPertencente(dispositivo);
                dispositivoRepository.deleteById(dispositivo.getId());
            }
            List<ConfiguracaoDeColeta> configsPersonalizadas = configuracaoDeColetaRepository.findAllByUsuarioPertencente(usuarioAtual.get());
            for (ConfiguracaoDeColeta config : configsPersonalizadas){
                configuracaoDeColetaRepository.deleteById(config.getId());
            }
            SecurityContextHolder.getContext().setAuthentication(null);
            usuarioRepository.deleteById(usuarioAtual.get().getId());
            return new ModelAndView("redirect:/login");
        } else {
            return new ModelAndView("redirect:/perfil");
        }

    }

}