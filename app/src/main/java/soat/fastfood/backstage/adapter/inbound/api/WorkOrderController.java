package soat.fastfood.backstage.adapter.inbound.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat.fastfood.backstage.application.usecase.retrieve.get.GetWorkOrderCommand;
import soat.fastfood.backstage.application.usecase.retrieve.get.GetWorkOrderUseCase;
import soat.fastfood.backstage.application.usecase.retrieve.list.ListWorkOrderCommand;
import soat.fastfood.backstage.application.usecase.retrieve.list.ListWorkOrderUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final GetWorkOrderUseCase getWorkOrderUseCase;
    private final ListWorkOrderUseCase listWorkOrderUseCase;

    @GetMapping("${id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        final var output = this.getWorkOrderUseCase.execute(new GetWorkOrderCommand(id));
        return ResponseEntity.ok(output);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam("status") String status) {
        final var outputs = this.listWorkOrderUseCase.execute(new ListWorkOrderCommand(status));
        return ResponseEntity.ok(outputs);
    }

}
