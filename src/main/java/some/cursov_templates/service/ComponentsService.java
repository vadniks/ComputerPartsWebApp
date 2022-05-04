package some.cursov_templates.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import some.cursov_templates.entity.PcComponent;
import some.cursov_templates.repo.ComponentsRepo;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static some.cursov_templates.Constants.*;
import static some.cursov_templates.entity.PcComponent.Type.AMOUNT;
import some.cursov_templates.entity.PcComponent.Type;

@RequiredArgsConstructor
@Service
public class ComponentsService {
    private final ComponentsRepo componentsRepo;
    @Value("classpath:static")
    private Resource resDir;

    public void addSelection(HttpServletRequest request, @Nullable String _id) {
        if (_id == null) return;
        val session = request.getSession();

        Items selections;
        if ((selections = (Items) session.getAttribute(SESSION_CHOSEN_ITEMS)) == null)
            selections = new Items(1);


        val id = Integer.parseInt(_id);
        val component = componentsRepo.getById(id);
        selections.add(new StringPairMap()
            .add(ATTRIBUTE_ON_CLICK, "/brw?type=" + component.type.name())
            .add(IMAGE, getPathForImage(component.image))
            .add(NAME, component.name)
            .add(TYPE, component.type.REAL_NAME)
            .add(COST, component.cost.toString())
            .add(DESCRIPTION, component.description));

        session.setAttribute(SESSION_CHOSEN_ITEMS, selections);
    }

    public Items getOverviewItems(HttpServletRequest request) {
        val session = request.getSession();
        val chosenItems = (Items) session.getAttribute(SESSION_CHOSEN_ITEMS);

        val items = new Items(AMOUNT);

        for (val type : Type.values()) {
            StringPairMap map;

            if ((map = findByType(chosenItems, type)) == null) {
                map = new StringPairMap();
                map.put(ATTRIBUTE_ON_CLICK, "/brw?type=" + type.name());
                map.put(IMAGE, getStubImagePathByType(type));
                map.put(NAME, UNSELECTED_DESCRIPTION);
                map.put(TYPE, type.REAL_NAME);
                map.put(COST, UNSELECTED_COST);
                map.put(DESCRIPTION, UNSELECTED_DESCRIPTION);
            }
            items.add(map);
        }
        return items;
    }

    @Nullable
    private static StringPairMap findByType(@Nullable Items items, Type type) {
        if (items == null) return null;

        for (val i : items)
            if (i.get(TYPE).equals(type.REAL_NAME))
                return i;
        return null;
    }

    //TODO: add image for water cooling system
    private static String getStubImagePathByType(Type type) {
        return RESOURCES_DIR_FROM_HTML + '/' + COMPONENT_IMAGE_PREFIX +
            type.name().toLowerCase() + COMPONENT_IMAGE_POSTFIX;
    }

    public Items getComponentsByType(String type) {
        val components = componentsRepo
            .getAllByType(Type.valueOf(type).TYPE);
        val items = new Items(components.size());

        for (val component : components) {
            val map = new StringPairMap();
            map.put(ID, Objects.requireNonNull(component.id).toString());
            map.put(IMAGE, getPathForImage(component.image));
            map.put(NAME, component.name);
            map.put(COST, component.cost.toString());

            items.add(map);
        }

        return items;
    }

    private static String getPathForImage(String image) {
        return RESOURCES_BACK_END_DIR_FROM_HTML + '/' +
            image + COMPONENT_IMAGE_POSTFIX;
    }

    @TestOnly
    void test() {
        System.out.println("gvbftghbtrhgbt");
        componentsRepo.save(new PcComponent(
            "Asus GeForce GTX 1650",
            Type.GPU,
            "Asus GeForce GTX 1650",
            750,
            "asus_1650_gpu"));
    }

    public StringPairMap getComponent(Integer id) {
        val _component = componentsRepo.findById(id);
        if (_component.isEmpty()) return null;
        val component = _component.get();

        return new StringPairMap()
            .add(ID, Objects.requireNonNull(component.id).toString())
            .add(NAME, component.name)
            .add(TYPE, component.type.name())
            .add(DESCRIPTION, component.description)
            .add(COST, component.cost.toString())
            .add(IMAGE, component.image);
    }

    // aka alias for the extended type
    public static class StringPairMap extends HashMap<String, String> {
        public StringPairMap() { super(); }
        public StringPairMap add(String a, String b) { put(a, b); return this; }
    }

    // aka alias for the extended type
    public static class Items extends ArrayList<StringPairMap>
    { public Items(int initialCapacity) { super(initialCapacity); } }
}
