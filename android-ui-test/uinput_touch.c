#include <linux/input.h>
#include <linux/uinput.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
static int fd, down, tracking=100;
static void ev(int type,int code,int value){struct input_event e={0};gettimeofday(&e.time,0);e.type=type;e.code=code;e.value=value;if(write(fd,&e,sizeof e)!=(ssize_t)sizeof e){perror("write input");exit(3);}}
static void sync_event(void){ev(EV_SYN,SYN_REPORT,0);}
int main(int argc,char **argv){
 int w=argc>1?atoi(argv[1]):1024,h=argc>2?atoi(argv[2]):768;
 setvbuf(stdout,0,_IOLBF,0);
 fd=open("/dev/uinput",O_WRONLY|O_NONBLOCK);
 if(fd<0){perror("open uinput");return 1;}
 ioctl(fd,UI_SET_EVBIT,EV_KEY);ioctl(fd,UI_SET_KEYBIT,BTN_TOUCH);ioctl(fd,UI_SET_KEYBIT,BTN_TOOL_FINGER);
 ioctl(fd,UI_SET_KEYBIT,KEY_VOLUMEUP);ioctl(fd,UI_SET_KEYBIT,KEY_VOLUMEDOWN);
 ioctl(fd,UI_SET_EVBIT,EV_ABS);ioctl(fd,UI_SET_EVBIT,EV_SYN);ioctl(fd,UI_SET_PROPBIT,INPUT_PROP_DIRECT);
 int axes[]={ABS_X,ABS_Y,ABS_PRESSURE,ABS_MT_SLOT,ABS_MT_TRACKING_ID,ABS_MT_POSITION_X,ABS_MT_POSITION_Y,ABS_MT_PRESSURE,ABS_MT_TOUCH_MAJOR};
 for(unsigned i=0;i<sizeof axes/sizeof *axes;i++)ioctl(fd,UI_SET_ABSBIT,axes[i]);
 struct uinput_user_dev d={0};snprintf(d.name,sizeof d.name,"SystemPulse Virtual Touchscreen");
 d.id.bustype=BUS_USB;d.id.vendor=0x18d1;d.id.product=0x4ee1;d.id.version=1;
 d.absmax[ABS_X]=d.absmax[ABS_MT_POSITION_X]=w-1;d.absmax[ABS_Y]=d.absmax[ABS_MT_POSITION_Y]=h-1;
 d.absmax[ABS_PRESSURE]=d.absmax[ABS_MT_PRESSURE]=255;d.absmax[ABS_MT_SLOT]=9;d.absmax[ABS_MT_TRACKING_ID]=65535;d.absmax[ABS_MT_TOUCH_MAJOR]=255;
 if(write(fd,&d,sizeof d)!=(ssize_t)sizeof d || ioctl(fd,UI_DEV_CREATE)<0){perror("create touchscreen");return 2;}
 const char *fifo="/data/local/tmp/systempulse-touch.fifo";
 if(mkfifo(fifo,0600)<0 && errno!=EEXIST){perror("mkfifo");return 4;}
 int pipefd=open(fifo,O_RDWR);if(pipefd<0){perror("open fifo");return 5;}
 FILE *in=fdopen(pipefd,"r");char line[256];printf("TOUCHSCREEN_READY %dx%d\n",w,h);
 while(fgets(line,sizeof line,in)){
  char type;int a,x=0,y=0;if(sscanf(line," %c %d %d %d",&type,&a,&x,&y)<3)continue;
  if(type=='K'){int key=x==24?KEY_VOLUMEUP:KEY_VOLUMEDOWN;ev(EV_KEY,key,a==0?1:0);sync_event();printf("KEY %d %d\n",a,x);continue;}
  if(type!='T')continue;
  a&=255;if(a!=0 && a!=1 && a!=2 && a!=3)continue;
  if(x<0)x=0;if(x>=w)x=w-1;if(y<0)y=0;if(y>=h)y=h-1;
  ev(EV_ABS,ABS_MT_SLOT,0);
  if(a==0){down=1;ev(EV_ABS,ABS_MT_TRACKING_ID,++tracking);ev(EV_KEY,BTN_TOUCH,1);ev(EV_KEY,BTN_TOOL_FINGER,1);}
  if(a==1 || a==3){ev(EV_ABS,ABS_MT_TRACKING_ID,-1);ev(EV_ABS,ABS_PRESSURE,0);ev(EV_ABS,ABS_MT_PRESSURE,0);ev(EV_KEY,BTN_TOUCH,0);ev(EV_KEY,BTN_TOOL_FINGER,0);down=0;}
  else if(down){ev(EV_ABS,ABS_MT_POSITION_X,x);ev(EV_ABS,ABS_MT_POSITION_Y,y);ev(EV_ABS,ABS_X,x);ev(EV_ABS,ABS_Y,y);ev(EV_ABS,ABS_PRESSURE,200);ev(EV_ABS,ABS_MT_PRESSURE,200);ev(EV_ABS,ABS_MT_TOUCH_MAJOR,8);}
  sync_event();printf("TOUCH %d %d %d\n",a,x,y);
 }
 ioctl(fd,UI_DEV_DESTROY);close(fd);return 0;
}
