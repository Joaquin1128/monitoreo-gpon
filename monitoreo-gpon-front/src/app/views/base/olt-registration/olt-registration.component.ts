import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';


@Component({
  selector: 'app-olt-registration',
  templateUrl: './olt-registration.component.html',
   imports: [ReactiveFormsModule]
 
})
export class OltRegistrationComponent {
  oltForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.oltForm = this.fb.group({
      name: ['', Validators.required],
      ipAddress: ['', [Validators.required, Validators.pattern('^(\\d{1,3}\\.){3}\\d{1,3}$')]],
      location: [''],
      model: [''],
      vendor: ['']
    });
  }

  onSubmit() {
    if (this.oltForm.valid) {
      console.log('OLT data submitted:', this.oltForm.value);
      // Aqui você pode chamar um serviço para enviar os dados à API
    } else {
      console.log('Formulário inválido');
    }
  }
}