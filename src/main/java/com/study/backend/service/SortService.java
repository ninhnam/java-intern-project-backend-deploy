package com.study.backend.service;

import com.study.backend.entity.Product;
import com.study.backend.entity.Sort;
import com.study.backend.entity.SortInfo;
import com.study.backend.exception.BadRequestException;
import com.study.backend.user.User;
import com.study.backend.exception.NotFoundException;
import com.study.backend.repository.ProductRepository;
import com.study.backend.repository.SortRepository;
import com.study.backend.repository.UserRepository;
import com.study.backend.request.SortRaw;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SortService {

    @Autowired
    private SortRepository sortRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;


    public Sort createSort(SortRaw sortRaw) {
        User user = userRepository.findById(sortRaw.getUser_id()).orElse(null);
        Product product = productRepository.findById(sortRaw.getProduct_id()).orElse(null);

        List<Sort> sortByUser = sortRepository.findByUserId(user.getId());
        Sort sortByProduct = sortByUser.stream()
                .filter(sort -> sort.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);

        if(sortByProduct != null) {
            sortByProduct.setQuantity(sortByProduct.getQuantity() + 1);
            return sortRepository.save(sortByProduct);
        } else {
            Sort sort = new Sort();
            sort.setQuantity(sortRaw.getQuantity());
            sort.setUser(user);
            sort.setProduct(product);
            return sortRepository.save(sort);
        }

    }

    public void updateSort(@Valid Sort sort) {
        // Perform any necessary validation
        sortRepository.save(sort);
    }


    public Sort getSortById(Long id) {
        Sort sort = sortRepository.findById(id).orElse(null);
        if (sort == null) {
            throw new NotFoundException("Sort with id: " + id + " not existing");
        }
        return sort;
    }

    public void deleteSortById(Long id) {
        Sort sort = getSortById(id);
        if (sort == null) {
            throw new NotFoundException("Sort with id: " + id + " not existing");
        }
        sortRepository.deleteById(id);
    }

    public List<SortInfo> getSortsByUserId(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if(user != null) {
            List<SortInfo> listSortInfo = user.getSorts().stream()
                    .map(sort -> new SortInfo(sort.getId(), sort.getProduct().getName(), sort.getProduct().getPrice(), sort.getQuantity()))
                    .collect(Collectors.toList());
            return listSortInfo;
        } else {
            return null;
        }

    }


//    public List<SortInfo> getSortsByUserId(Long id) {
//        User user = userRepository.findById(id).orElse(null);
//
//        if(user != null) {
//            Object listSortInfo = user.getSorts().stream()
//                    .map(sort -> getSortInfo(sort))
//                    .collect(Collectors.toList());
//            return (List<SortInfo>) listSortInfo;
//        } else {
//            return null;
//        }
//
//    }


    public SortInfo getSortInfo(Sort sort) {
        Product product = productRepository.findById(sort.getProduct().getId()).orElse(null);
        return new SortInfo(sort.getId(), product.getName(), product.getPrice(), sort.getQuantity());
    }
}