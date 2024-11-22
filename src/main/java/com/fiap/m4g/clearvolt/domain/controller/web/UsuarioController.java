package com.fiap.m4g.clearvolt.domain.controller.web;

import com.fiap.m4g.clearvolt.domain.entity.Role;
import com.fiap.m4g.clearvolt.domain.entity.Usuario;
import com.fiap.m4g.clearvolt.domain.repository.RoleRepository;
import com.fiap.m4g.clearvolt.domain.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class CadastroController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean autenticado() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()));
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
            mv.addObject("errorMessage","Já  existe");
            mv.addObject("autenticado",autenticado());
            return mv;

        } else {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            Role role = roleRepository.findByName("ROLE_FREE");
            usuario.setUserRole(role);
            usuarioRepository.save(usuario);
            System.out.println("CADASTRADO");
            SecurityContextHolder.clearContext();
            ModelAndView mv = new ModelAndView("redirect:/login");
            return mv;

        }
    }
}