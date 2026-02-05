package com.example.backend.service;

import com.example.backend.dto.DeviceDto;
import com.example.backend.entity.Department;
import com.example.backend.entity.Device;
import com.example.backend.repository.DepartmentRepository;
import com.example.backend.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void getAllDevices_shouldReturnList() {

        Device d1 = new Device();
        Device d2 = new Device();

        when(deviceRepository.findAll())
                .thenReturn(List.of(d1, d2));

        List<DeviceDto> result = deviceService.getAllDevices();

        assertEquals(2, result.size());
    }

    @Test
    void getDeviceById_shouldReturnDevice() {

        Device device = new Device();
        device.setId(1L);

        when(deviceRepository.findById(1L))
                .thenReturn(Optional.of(device));

        DeviceDto dto = deviceService.getDeviceById(1L);

        assertEquals(1L, dto.getId());
    }

    @Test
    void createDevice_shouldSave() {

        DeviceDto dto = new DeviceDto();
        dto.setName("PC");
        dto.setSerialNumber("123");
        dto.setDepartmentId(1L);

        Department dep = new Department();
        dep.setId(1L);

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(dep));

        when(deviceRepository.findSerialNumber("123"))
                .thenReturn(Optional.empty());

        when(deviceRepository.save(any(Device.class)))
                .thenAnswer(i -> i.getArgument(0));

        DeviceDto result = deviceService.createDevice(dto, null);

        assertEquals("PC", result.getName());
    }

    @Test
    void createDevice_shouldThrowIfDepartmentNotFound() {

        DeviceDto dto = new DeviceDto();
        dto.setDepartmentId(99L);

        when(departmentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> deviceService.createDevice(dto, null));
    }

    @Test
    void deleteDevice_shouldCallRepo() {

        deviceService.deleteDevice(1L);

        verify(deviceRepository).deleteById(1L);
    }

    @Test
    void createDevice_withImage_shouldWork() throws Exception {

        DeviceDto dto = new DeviceDto();
        dto.setName("PC");
        dto.setSerialNumber("456");
        dto.setDepartmentId(1L);

        Department dep = new Department();
        dep.setId(1L);

        MockMultipartFile file =
                new MockMultipartFile(
                        "image",
                        "test.png",
                        "image/png",
                        "data".getBytes()
                );

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(dep));

        when(deviceRepository.findSerialNumber("456"))
                .thenReturn(Optional.empty());

        when(deviceRepository.save(any(Device.class)))
                .thenAnswer(i -> i.getArgument(0));

        DeviceDto result = deviceService.createDevice(dto, file);

        assertNotNull(result);
    }
}