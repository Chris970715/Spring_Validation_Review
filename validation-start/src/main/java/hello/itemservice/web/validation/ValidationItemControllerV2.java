package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import hello.itemservice.domain.item.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/validation/v2/items")
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemVaildator itemVaildator;

    @Autowired
    public ValidationItemControllerV2(ItemRepository itemRepository, ItemVaildator itemVaildator) {
        this.itemRepository = itemRepository;
        this.itemVaildator = itemVaildator;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("{itemID}")
    public String item(@PathVariable Long itemID, Model model) {
        Item item = itemRepository.findById(itemID);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /*
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, BindingResult  bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(), false, null, null,"Name is required"));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item","price",item.getPrice(), false, null, null,"Price must be between 1 and 1000000"));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 10000) {
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(), false, null,null,"Quantity must be between 1 and 10000"));
        }

        //Global vaildation
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
              bindingResult.addError(new ObjectError("item","Price must be between 1 and 10000"));
            }
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addFlashAttribute("savedItem", savedItem.getId());
        redirectAttributes.addFlashAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }

        @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult  bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            //errors.addError(new FieldError("item","quantity",item.getQuantity(), false, new String[]{"max.item.quantity"},new Object[]{1, 10000}, null));
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //Global vaildation
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();

            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addFlashAttribute("savedItem", savedItem.getId());
        redirectAttributes.addFlashAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }
    */

    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        itemVaildator.validate(item, bindingResult);

        if(bindingResult.hasErrors()) {
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addFlashAttribute("savedItem", savedItem.getId());
        redirectAttributes.addFlashAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("{itemId}/edit")
    public String editItem(@PathVariable Long itemId ,@ModelAttribute Item item){
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }


}
