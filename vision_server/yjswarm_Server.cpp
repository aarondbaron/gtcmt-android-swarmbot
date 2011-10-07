#include "stdafx.h"
#include "opencv2/video/tracking.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include <iostream>
#include <ctype.h>

#define network

using namespace System;
using namespace System::IO;
using namespace System::Net;
using namespace System::Net::Sockets;
using namespace System::Text;
using namespace System::Threading;
using namespace cv;
using namespace std;
Mat image;

bool backprojMode = false;
bool selectObject = false;
int trackObject = 0;
bool showHist = true;
Point origin;
Rect selection;
RotatedRect trackBox;
int vmin = 10, vmax = 256, smin = 30;
bool connected=false;

void onMouse( int event, int x, int y, int, void* )
{
    if( selectObject )
    {
        selection.x = MIN(x, origin.x);
        selection.y = MIN(y, origin.y);
        selection.width = std::abs(x - origin.x);
        selection.height = std::abs(y - origin.y);

        selection &= Rect(0, 0, image.cols, image.rows);
    }

    switch( event )
    {
    case CV_EVENT_LBUTTONDOWN:
        origin = Point(x,y);
        selection = Rect(x,y,0,0);
        selectObject = true;
        break;
    case CV_EVENT_LBUTTONUP:
        selectObject = false;
        if( selection.width > 0 && selection.height > 0 )
            trackObject = -1;
        break;
    }
}

void help()
{
    cout << "\nThis is a demo that shows mean-shift based tracking\n"
			"You select a color objects such as your face and it tracks it.\n"
			"This reads from video camera (0 by default, or the camera number the user enters\n"
			"Usage: \n"
            "	./camshiftdemo [camera number]\n";

    cout << "\n\nHot keys: \n"
			"\tESC - quit the program\n"
			"\tc - stop the tracking\n"
			"\tb - switch to/from backprojection view\n"
			"\th - show/hide object histogram\n"
			"\tp - pause video\n"
            "To initialize tracking, select the object with mouse\n";
}

const char* keys = 
{
	"{1|  | 0 | camera number}"
};

public ref class Server{
	
public:
	
	NetworkStream^ stream;
	TcpClient^ client;
	NetworkStream^ stream1;
	TcpClient^ client1;
public:
	void visionTrack(){
#ifdef network
		Console::WriteLine("Waiting for socket connection establishment");
		Int32 port = 8080;
		IPAddress^ localAddr = IPAddress::Parse( "143.215.102.16" );
		TcpListener^ server = gcnew TcpListener( localAddr,port );

		server->Start();	
		client = server->AcceptTcpClient();
		stream = client->GetStream();
//		connected=true;
		Console::WriteLine( "Connected to 0" );

		client1 = server->AcceptTcpClient();
		stream1 = client1->GetStream();
		connected=true;
		Console::WriteLine( "Connected to 1" );

#endif

		help();
		VideoCapture cap;
		Rect trackWindow;
		RotatedRect trackBox;

		Rect trackWindow2;
		RotatedRect trackBox2;

		// Buffer for reading data
		array<Byte>^bytes = gcnew array<Byte>(256);
		System::String^ data = nullptr;

		int hsize = 16;
		float hranges[] = {0,180};
		const float* phranges = hranges;
		int camNum=0;
		cap.open(camNum);

		if( !cap.isOpened() )
		{
    		help();
			cout << "***Could not initialize capturing...***\n";
			cout << "Current parameter's value: \n";
			Console::ReadLine();
			exit(-1);

		}

		namedWindow( "Histogram", 0 );
		namedWindow( "CamShift Demo", 0 );
		setMouseCallback( "CamShift Demo", onMouse, 0 );
		createTrackbar( "Vmin", "CamShift Demo", &vmin, 256, 0 );
		createTrackbar( "Vmax", "CamShift Demo", &vmax, 256, 0 );
		createTrackbar( "Smin", "CamShift Demo", &smin, 256, 0 );

		Mat frame, hsv, hue, mask, hist, histimg = Mat::zeros(200, 320, CV_8UC3), backproj;
		Mat hist2, histimg2 = Mat::zeros(200, 320, CV_8UC3), backproj2;
		int selectObj=0;

		bool paused = false;
    
		while(1)
		{
			if( !paused )
			{
				cap >> frame;
				if( frame.empty() )
					break;
			}

			frame.copyTo(image);
        
			if( !paused )
			{
				cvtColor(image, hsv, CV_BGR2HSV);

				if( trackObject )
				{
					int _vmin = vmin, _vmax = vmax;

					inRange(hsv, Scalar(0, smin, MIN(_vmin,_vmax)),
							Scalar(180, 256, MAX(_vmin, _vmax)), mask);
					int ch[] = {0, 0};
					hue.create(hsv.size(), hsv.depth());
					mixChannels(&hsv, 1, &hue, 1, ch, 1);
					if( trackObject < 0 &&selectObj==0)
					{
						Mat roi(hue, selection), maskroi(mask, selection);
                    
						calcHist(&roi, 1, 0, maskroi, hist, 1, &hsize, &phranges);
						normalize(hist, hist, 0, 255, CV_MINMAX);
                    
						trackWindow = selection;
						trackObject = 1;

						histimg = Scalar::all(0);
						int binW = histimg.cols / hsize;
						Mat buf(1, hsize, CV_8UC3);
						for( int i = 0; i < hsize; i++ )
							buf.at<Vec3b>(i) = Vec3b(saturate_cast<uchar>(i*180./hsize), 255, 255);
						cvtColor(buf, buf, CV_HSV2BGR);
                        
						for( int i = 0; i < hsize; i++ )
						{
							int val = saturate_cast<int>(hist.at<float>(i)*histimg.rows/255);
							rectangle( histimg, Point(i*binW,histimg.rows),
									   Point((i+1)*binW,histimg.rows - val),
									   Scalar(buf.at<Vec3b>(i)), -1, 8 );
						}
						selectObj=1;
					}

					if( trackObject < 0 &&selectObj==1)
					{
						Mat roi(hue, selection), maskroi(mask, selection);
                    
						calcHist(&roi, 1, 0, maskroi, hist2, 1, &hsize, &phranges);
						normalize(hist2, hist2, 0, 255, CV_MINMAX);
                    
						trackWindow2 = selection;
						trackObject = 1;

						histimg2 = Scalar::all(0);
						int binW = histimg2.cols / hsize;
						Mat buf(1, hsize, CV_8UC3);
						for( int i = 0; i < hsize; i++ )
							buf.at<Vec3b>(i) = Vec3b(saturate_cast<uchar>(i*180./hsize), 255, 255);
						cvtColor(buf, buf, CV_HSV2BGR);
                        
						for( int i = 0; i < hsize; i++ )
						{
							int val = saturate_cast<int>(hist2.at<float>(i)*histimg2.rows/255);
							rectangle( histimg2, Point(i*binW,histimg2.rows),
									   Point((i+1)*binW,histimg2.rows - val),
									   Scalar(buf.at<Vec3b>(i)), -1, 8 );
						}
						selectObj=2;
					}
				
					calcBackProject(&hue, 1, 0, hist, backproj, &phranges);
					backproj &= mask;

					if(trackWindow.width>=0&&trackWindow.height>=0)
					{					
						trackBox = CamShift(backproj, trackWindow,
										TermCriteria( CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1 ));
						//printf("OBJ1 %d\t%d\n",trackWindow.width,trackWindow.height);
					}
					if( trackWindow.area() <= 1)
					{
						int cols = backproj.cols, rows = backproj.rows, r = (MIN(cols, rows) + 5)/6;
						trackWindow = Rect(trackWindow.x - r, trackWindow.y - r,
										   trackWindow.x + r, trackWindow.y + r) &
									  Rect(5, 5, cols, rows);
					}
					if( backprojMode )
						cvtColor( backproj, image, CV_GRAY2BGR );
					if(trackBox.size.height>=0&&trackBox.size.width>=0){
						ellipse( image, trackBox, Scalar(0,0,255), 3, CV_AA );
	#ifdef network
						data = "position,"+(int)(trackBox.center.x)+","+(int)(trackBox.center.y)+",1"+"\n\r";
						array<Byte>^msg = Text::Encoding::ASCII->GetBytes( data );
						stream->Write( msg, 0, msg->Length );
						stream1->Write( msg, 0, msg->Length );
	//					Console::WriteLine( "Sent: {0}", data );
	#endif
					}

					if(selectObj==2)
					{
						calcBackProject(&hue, 1, 0, hist2, backproj2, &phranges);
						backproj2 &= mask;
						if(trackWindow2.width>=0&&trackWindow2.height>=0)
						{					
							trackBox2 = CamShift(backproj2, trackWindow2,
											TermCriteria( CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1 ));
//							printf("OBJ2 %d\t%d\n",trackWindow2.width,trackWindow2.height);
						}
						if( trackWindow2.area() <= 1)
						{
							int cols = backproj2.cols, rows = backproj2.rows, r = (MIN(cols, rows) + 5)/6;
							trackWindow = Rect(trackWindow2.x - r, trackWindow2.y - r,
											   trackWindow2.x + r, trackWindow2.y + r) &
										  Rect(5, 5, cols, rows);
						}
						if( backprojMode )
							cvtColor( backproj2, image, CV_GRAY2BGR );
						if(trackBox2.size.height>=0&&trackBox2.size.width>=0){
							ellipse( image, trackBox2, Scalar(0,0,255), 3, CV_AA );
	#ifdef network
						
							data = "position,"+(int)trackBox2.center.x+","+(int)trackBox2.center.y+",2"+"\n\r";
							array<Byte>^msg = Text::Encoding::ASCII->GetBytes( data );
							stream->Write( msg, 0, msg->Length );
							stream1->Write( msg, 0, msg->Length );
	//						Console::WriteLine( "Sent: {0}", data );
	#endif
						}
					}
				}
			}
			else if( trackObject < 0 )
				paused = false;

			if( selectObject && selection.width > 0 && selection.height > 0 )
			{
				Mat roi(image, selection);
				bitwise_not(roi, roi);
			}

			imshow( "CamShift Demo", image );
			imshow( "Histogram", histimg );
			imshow( "Histogram2", histimg2 );

			char c = (char)waitKey(10);
			if( c == 27 )
			{	// Shutdown and end connection
	#ifdef network
				client->Close();
	//			client1->Close();
				exit(1);
	#endif
				break;
			}
			switch(c)
			{
			case 'b':
				backprojMode = !backprojMode;
				break;
			case 'c':
				trackObject = 0;
				histimg = Scalar::all(0);
				break;
			case 'h':
				showHist = !showHist;
				if( !showHist )
					destroyWindow( "Histogram" );
				else
					namedWindow( "Histogram", 1 );
				break;
			case 'p':
				paused = !paused;
				break;
			default:
				;
			}
		}
	}

	void controller(){
	
		// Buffer for reading data
		System::String^ data = nullptr;
		System::String^ send = nullptr;

		while(!connected){}
		while(1)
		{
			data =Console::ReadLine();
			if(data=="w"){
				send="forward1";
			}else if(data=="a"){
				send="rotleft1";
			}else if(data=="s"){
				send="backward1";
			}else if(data=="d"){
				send="rotright1";
			}else if(data=="x"){
				send="stop1";
			}else if(data=="i"){
				send="forward2";
			}else if(data=="j"){
				send="rotleft2";
			}else if(data=="k"){
				send="backward2";
			}else if(data=="l"){
				send="rotright2";
			}else if(data=="m"){
				send="stop2";
			}

			if(data=="exit")
			{
				exit(1);
			}
			send+="\r\n";
			array<Byte>^msg = Text::Encoding::ASCII->GetBytes(send);
			stream->Write( msg, 0, msg->Length );
			Console::WriteLine(send);
		}
	}
};

int main( int argc, const char** argv )
{
	Server ^ o1 = gcnew Server();
	Thread^ t1 = gcnew Thread(gcnew ThreadStart(o1, &Server::visionTrack));
	t1->Start();
	Thread::Sleep(100);

	Thread^ t2 = gcnew Thread(gcnew ThreadStart(o1, &Server::controller));
	t2->Start();
//    return 0;
}
