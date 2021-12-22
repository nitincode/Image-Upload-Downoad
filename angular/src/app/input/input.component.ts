import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpClientModule, HttpClient, HttpRequest, HttpResponse, HttpEventType } from '@angular/common/http';
import { Observable } from 'rxjs';
import { saveAs } from 'file-saver';
import * as FileSaver from 'file-saver';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css']
})
export class InputComponent implements OnInit {

  percentDone: number;
  uploadSuccess: boolean  = false;
  selectedFile: File;
  imageName: any;
  retrievedImgSize: number  =1024*1024;
  fileError: boolean = false;

  constructor(
    private http: HttpClient) { }

  ngOnInit() {

  }

  public onFileChanged(event) {
    //Select File
    this.selectedFile = event.target.files[0];
    console.log(this.selectedFile.size);
    if(this.selectedFile.size/this.retrievedImgSize>15)
    {
      this.fileError =true;
    }
  
  }
  onUpload() {
    //FormData API provides methods and properties to allow us easily prepare form data to be sent with POST HTTP requests.
    const uploadImageData = new FormData();
    uploadImageData.append('imageFile', this.selectedFile, this.selectedFile.name);
    //Make a call to the Spring Boot Application to save the image
    this.http.post('http://localhost:8080/image/upload',
      uploadImageData, { responseType: 'text'})
      .subscribe((response:any) => {
       this.uploadSuccess =true;
      },
      (error) => {     //error() callback
       // console.error('Request failed with error')
      
      },
      );
  }


  downloadFile(): void {
  
    this.download().subscribe(data => {
      console.log(data);
      // save image to disk
      const blob = new Blob([data]);
      this.saveAsBlob(blob)
    });
  }

  download(): Observable<Blob> {
    return this.http.get('http://localhost:8080/image/get/' + this.imageName, {
      responseType: 'blob'
    });
  } 

  
  private saveAsBlob(data: any) {
    const file = new File([data], 'report.png');
    FileSaver.saveAs(file);
}




downloadFileEXcel(): void {
  
  this.download1().subscribe(data => {
    console.log(data);
    // save image to disk
    const blob = new Blob([data]);
    this.saveAsBlob1(blob)
  });
}

download1(): Observable<Blob> {
  return this.http.get('http://localhost:8080/api/v2/downloadExcel/', {
    responseType: 'blob'
  });
} 


private saveAsBlob1(data: any) {
  const file = new File([data], 'report.xlsx');
  FileSaver.saveAs(file);
}

}