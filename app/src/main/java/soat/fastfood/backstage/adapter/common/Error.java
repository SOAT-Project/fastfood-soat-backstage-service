package soat.fastfood.backstage.adapter.common;

import java.util.List;

public record Error(String type, String title, Integer status, String detail, String instance, List<ErrorMessage> messages) {
}
