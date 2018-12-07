function varargout = EIT_recon(varargin)
% EIT_RECON MATLAB code for EIT_recon.fig
%      EIT_RECON, by itself, creates a new EIT_RECON or raises the existing
%      singleton*.
%
%      H = EIT_RECON returns the handle to a new EIT_RECON or the handle to
%      the existing singleton*.
%
%      EIT_RECON('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in EIT_RECON.M with the given input arguments.
%
%      EIT_RECON('Property','Value',...) creates a new EIT_RECON or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before EIT_recon_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to EIT_recon_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help EIT_recon

% Last Modified by GUIDE v2.5 15-Feb-2017 20:23:22

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @EIT_recon_OpeningFcn, ...
                   'gui_OutputFcn',  @EIT_recon_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before EIT_recon is made visible.
function EIT_recon_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to EIT_recon (see VARARGIN)

% Choose default command line output for EIT_recon
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

set(handles.pop_colormap,'Value',4);
axes(handles.axes1);
axis off;
global data_path;
global option_scale;
global option_RM;
global option_color;
global change_ref;
data_path = '';
option_scale = 1;
option_RM = 1;
option_color = 4;
change_ref = 0;

% UIWAIT makes EIT_recon wait for user response (see UIRESUME)
% uiwait(handles.figure1);

% --- Outputs from this function are returned to the command line.
function varargout = EIT_recon_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;

function edit_path_Callback(hObject, eventdata, handles)
% hObject    handle to edit_path (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_path as text
%        str2double(get(hObject,'String')) returns contents of edit_path as a double

% --- Executes during object creation, after setting all properties.
function edit_path_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

% --- Executes on button press in push_path.
function push_path_Callback(hObject, eventdata, handles)
global data_path
[data_path]=uigetdir('C:\');
set(handles.edit_path,'string',data_path);

% --- Executes on button press in toggle_recon.
function toggle_recon_Callback(hObject, eventdata, handles)
global data_path
global option_scale
global option_color;
global option_RM;
global change_ref;
% data_path = 'C:\Users\Jang\Desktop\test';

% import files
switch option_RM
    case 1
        load('1Circle.mat');
    case 2
        load('2Human.mat');
    case 3
        load('3Pig.mat');
end
load('Cmap1.mat');
load('Cmap2.mat');
load('Cmap3.mat');
load('Cmap4.mat');
Cmap1([1 end],:) = [];
Cmap2([1 end],:) = [];
Cmap3([1 end],:) = [];
Cmap4([1 end],:) = [];
inv_Sense = Data.inv_Sense_weighted_avg*Data.Proj_Mat;

%%
dirlist = dir(data_path);
dirlist(1:2) = [];
num_rawdata = length(dirlist);

temp2 = FxEIT_RawImport(strcat(data_path,'\',dirlist(num_rawdata).name));

[~,Zmin] = min(sum(temp2));
ref = temp2(:,Zmin);

axes(handles.axes1);
h1 = patch('Faces',Data.Element,'Vertices' ,Data.Node,'FaceVertexCData' ,zeros(size(Data.Element)),'FaceColor' ,'flat' ,'EdgeColor' ,'None' );
axis equal; axis off;
% caxis([-max_value max_value*0.2]);
colormap(Cmap4);
drawnow;
tic;
cnt = 1;
cnt_stop = 0;
cnt_max = 1;
max_value = 1;
max_update = 200;
max_stack = zeros(max_update,1);
nSkip = 2;
running_delay = 0;
% set(handles.edit_path,'string',[data_path]);

while get(hObject,'Value')
    dirlist = dir(data_path);
    dirlist(1:2) = [];
    if length(dirlist) > num_rawdata
        pause_time = toc;
        if pause_time > 0.1
            running_delay = running_delay + pause_time/round(size(temp2,2)/(nSkip + 1));
        else
            running_delay = running_delay*0.99;
        end
        
        num_rawdata = length(dirlist);
        % data import
        temp2 = FxEIT_RawImport(strcat(data_path,'\',dirlist(num_rawdata).name));
%         temp3 = inv_Sense*(temp2-repmat(ref,1,size(temp2,2)));
        axes(handles.axes1);
        for i = 1:(nSkip+1):size(temp2,2)
            if change_ref == 1
                ref = temp2(:,i);
                change_ref = 0;
            end
            temp3 = inv_Sense*(temp2(:,i)-ref);
            
            % draw image
            if ishandle(h1)
                delete(h1);
            end
            
            h1 = patch('Faces',Data.Element,'Vertices' ,Data.Node,'FaceVertexCData' ,temp3,'FaceColor' ,'flat' ,'EdgeColor' ,'None' );
            axis equal; axis off;
            
            % scale set
            if option_scale == 1
                if cnt_max > max_update
                    cnt_max = 1;
                end
                max_stack(cnt_max) = max(abs(temp3)); cnt_max = cnt_max + 1;
                max_value = max(max_stack);
            elseif option_scale == 2
                max_value = abs(str2num(get(handles.edit_scale,'string')));
            end
            
            % color style
            switch option_color
                case 1
                    colormap(Cmap1);
                    caxis([-max_value*0.9 max_value*0.9]);
                case 2
                    colormap(Cmap2(end:-1:1,:));
                    caxis([-max_value*0.9 max_value*0.9*0.2]);
                case 3
                    colormap(Cmap3);
                    caxis([-max_value*0.9 max_value*0.9]);
                case 4
                    colormap(Cmap4(end:-1:1,:));
                    caxis([-max_value*0.9*0.2 max_value*0.9]);
            end
            
            drawnow;
            cnt = cnt + 1;
            cnt_stop = 0;
            set(handles.text_scannum,'string',num2str(cnt));
            set(handles.text_filename,'string',dirlist(num_rawdata).name);
            pause(running_delay);
        end   
        tic;
%         if running_time < 11
%             running_delay = (running_time-10)/size(temp2,2)/(nSkip+1);
%         elseif running_time > 11
%             if (running_time-10) > running_delay*size(temp2,2)/(nSkip+1)
%                 nSkip = nSkip + 1;
%             else
%                 running_delay = (running_time-10)/size(temp2,2)/(nSkip+1);
%             end
%         end
    else        
        if cnt_stop > 1000000000
            break;
        end
        cnt_stop = cnt_stop + 1;
%         running_delay = running_delay + 0.1;
        pause(0.05);
    end
end
if ishandle(h1)
    delete(h1);
end

% % cd(data_path);
% dirlist = dir(data_path);
% cnt = length(dirlist)-10;
% if cnt < 1
%     cnt = 1;
% end
% cnt = 1;
% 
% fid = fopen(strcat(data_path,'\',num2str(cnt),'Scan.txt'));
% temp = textscan(fid, '%f%f%f%f');
% fclose(fid);
% 
% temp2 = sqrt(temp{1,3}.^2 + temp{1,4}.^2);
% temp2(257:end) = [];
% temp2(mask) = [];
% ref = temp2;
% 
% axes(handles.axes1);
% h1 = patch('Faces',Data.Element,'Vertices' ,Data.Node,'FaceVertexCData' ,zeros(size(Data.Element)),'FaceColor' ,'flat' ,'EdgeColor' ,'None' );
% axis equal; axis off;
% % caxis([-max_value max_value*0.2]);
% colormap(Cmap4);
% drawnow;
% 
% stop = 0;
% cnt_stop = 0;
% max_value = 1;
% cnt_max = 1;
% max_update = 200;
% temp_max = zeros(max_update,1);
% % set(handles.edit_path,'string',[data_path]);
% 
% while get(hObject,'Value')
%     try
%         % data import
%         fid = fopen(strcat(data_path,'\',num2str(cnt),'Scan.txt'));
%         temp = textscan(fid, '%f%f%f%f');
%         fclose(fid);
%         
%         temp2 = sqrt(temp{1,3}.^2 + temp{1,4}.^2);
%         temp2(257:end) = [];
%         temp2(mask) = [];
%         temp3 = inv_Sense*(temp2-ref);
%         
%         % draw image
%         if ishandle(h1)
%             delete(h1);
%         end
%         axes(handles.axes1);
%         h1 = patch('Faces',Data.Element,'Vertices' ,Data.Node,'FaceVertexCData' ,-temp3,'FaceColor' ,'flat' ,'EdgeColor' ,'None' );
%         axis equal; axis off; 
%         
%         % scale set
%         if option_scale == 1
%             if cnt_max > max_update
%                 cnt_max = 1;
%             end
%             temp_max(cnt_max) = max(abs(temp3)); cnt_max = cnt_max + 1;
%             max_value = max(temp_max);
%         elseif option_scale == 2
%             max_value = abs(str2num(get(handles.edit_scale,'string')));
%         end
%         
%         % color style
%         switch option_color
%             case 1
%                 colormap(Cmap1);
%                 caxis([-max_value*0.9 max_value*0.9]); 
%             case 2
%                 colormap(Cmap2);
%                 caxis([-max_value*0.9 max_value*0.9]);
%             case 3
%                 colormap(Cmap3);
%                 caxis([-max_value*0.9 max_value*0.9]);
%             case 4
%                 colormap(Cmap4(end:-1:1,:));
%                 caxis([-max_value*0.2*0.9 max_value*0.9]);
%         end
%         
%         drawnow;
%         cnt = cnt + 1;
%         cnt_stop = 0;
%         set(handles.text_scannum,'string',num2str(cnt));
%     catch
%         if exist(strcat(data_path,'\',num2str(cnt+1),'Scan.txt')) == 2
%             cnt = cnt + 1;
%         elseif exist(strcat(data_path,'\',num2str(cnt+2),'Scan.txt')) == 2
%             cnt = cnt + 2;
%         end
%         if cnt_stop > 1000000000
%             stop = 1;
%         end
%         cnt_stop = cnt_stop + 1;
%         pause(0.05);
%     end
% end
% if ishandle(h1)
%     delete(h1);
% end

function edit_scale_Callback(hObject, eventdata, handles)

% --- Executes during object creation, after setting all properties.
function edit_scale_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

% --- Executes when selected object is changed in uibuttongroup1.
function uibuttongroup1_SelectionChangeFcn(hObject, eventdata, handles)
global option_scale
switch hObject
    case handles.radio_auto
        option_scale = 1;
    case handles.radio_manual
        option_scale = 2;
end

% --- Executes on selection change in pop_colormap.
function pop_colormap_Callback(hObject, eventdata, handles)
global option_color;
option_color = get(handles.pop_colormap,'Value');

% --- Executes during object creation, after setting all properties.
function pop_colormap_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

% --- Executes on selection change in pop_RM.
function pop_RM_Callback(hObject, eventdata, handles)
global option_RM;
option_RM = get(handles.pop_RM,'Value');

% --- Executes during object creation, after setting all properties.
function pop_RM_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

function [EIT_data] = FxEIT_RawImport(EIT_data_path)
scan_num = 512; % 1 raw data have 512 scan data
data_num = 256; % depend on protocol

fid = fopen(EIT_data_path);
raw_data = fread(fid,data_num*scan_num*6,'uint8');
% scan_num = length(raw_data) / data_num*6;
raw_data = reshape(raw_data,data_num*6,scan_num);
fclose(fid);

cnt = 1;
for j = 1:512
    temp = raw_data(:,j);
    temp = reshape(temp,6,256);
    temp(1,:)=temp(1,:)-128;
    temp(1:2,:) = temp([2 1],:);
    temp(3,:) = temp(3,:).*256 + temp(4,:);
    temp(4,:) = temp(5,:).*256 + temp(6,:);
    temp(5:6,:) = [];
    temp(temp>32767) = temp(temp>32767) - 65536;
    temp = temp';
    temp = temp([1:16:256 2:16:256 3:16:256 4:16:256 5:16:256 6:16:256 7:16:256 8:16:256 9:16:256 10:16:256 11:16:256 12:16:256 13:16:256 14:16:256 15:16:256 16:16:256],:);
    scan_data = temp;
    signed = scan_data(:,3);
    signed(signed>=0) = 1;
    signed(signed<0) = -1;
    
    EIT_data(:,cnt) = signed.*sqrt(scan_data(:,3).^2 + scan_data(:,4).^2);
    clear temp;
    cnt = cnt + 1;
end
clear raw_data;
mask = [1,2,16,17,18,19,34,35,36,51,52,53,68,69,70,85,86,87,102,103,104,119,120,121,136,137, ...
138,153,154,155,170,171,172,187,188,189,204,205,206,221,222,223,238,239,240,241,255,256;];
EIT_data(mask,:) = [];


% --- Executes on button press in push_ref.
function push_ref_Callback(hObject, eventdata, handles)
global change_ref;
change_ref = 1;
