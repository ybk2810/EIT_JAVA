%% path
clear all; close all; clc;
Data_path = 'C:\Users\Developer\Desktop\test';
load('C:\Users\Developer\Desktop\Realtime_monitoring\Data_result.mat');
load('C:\Users\Developer\Desktop\Realtime_monitoring\Cmap4.mat');
Cmap4([1 end],:) = [];
%%
inv_Sense = Data.inv_Sense_weighted_avg*Data.Proj_Mat;
mask = [1,2,16,17,18,19,34,35,36,51,52,53,68,69,70,85,86,87,102,103,104,119,120,121,136,137, ...
138,153,154,155,170,171,172,187,188,189,204,205,206,221,222,223,238,239,240,241,255,256;];
cd(Data_path);

fid = fopen('1Scan.txt');
temp = textscan(fid, '%f%f%f%f');
fclose(fid);

temp2 = sqrt(temp{1,3}.^2 + temp{1,4}.^2);
temp2(257:end) = [];
temp2(mask) = [];
ref = temp2;

h1 = patch('Faces',Data.Element,'Vertices' ,Data.Node,'FaceVertexCData' ,ref,'FaceColor' ,'flat' ,'EdgeColor' ,'None' );
        axis equal; axis off;

cnt = 2;
stop = 0;
cnt_stop = 0;
max_value = 1;
while(stop~=1)
    try
%         disp('1');
        cd(Data_path);
        fid = fopen(strcat(num2str(cnt),'Scan.txt'));
        temp = textscan(fid, '%f%f%f%f');
        fclose(fid);
%         disp('2');
        temp2 = sqrt(temp{1,3}.^2 + temp{1,4}.^2);
        temp2(257:end) = [];
        temp2(mask) = [];
        temp3 = inv_Sense*(temp2-ref);
        
        if max_value < max(abs(temp3))
            max_value = max(abs(temp3));
        end
        
        if ishandle(h1)
            delete(h1);
        end
        h1 = patch('Faces',Data.Element,'Vertices' ,Data.Node,'FaceVertexCData' ,temp3,'FaceColor' ,'flat' ,'EdgeColor' ,'None' );
        axis equal; axis off;
        caxis([-max_value*0.9 max_value*0.2*0.9]);
        colormap(Cmap4);
        drawnow;
        
        cnt = cnt + 1;
        cnt_stop = 0;
        disp(num2str(cnt));
    catch
%         disp('3');
%         cd(Data_path);
%         if exist(strcat(num2str(cnt+1),'Scan.txt')) == 2
%             cnt = cnt + 1;
%         else exist(strcat(num2str(cnt+2),'Scan.txt')) == 2
%             cnt = cnt + 2;
%         end
%         disp('4');
        if cnt_stop > 100
            stop = 1;
        end
        cnt_stop = cnt_stop + 1;
        pause(0.1);
    end
end